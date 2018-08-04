package moe.kyokobot.bot.manager.impl;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import io.sentry.Sentry;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;
import moe.kyokobot.bot.module.CoreModule;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.module.ModuleDescription;
import moe.kyokobot.bot.util.CommonUtil;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.GsonUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class SimpleModuleManagerOld implements ModuleManager {
    private static final File MODULES_DIR = new File(System.getProperty("kyoko.plugindir", "modules"));

    private final ShardManager shardManager;
    private final DatabaseManager databaseManager;
    private final I18n i18n;
    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;
    private final Logger logger;
    private HashMap<String, KyokoModule> modules;
    private HashMap<String, URLClassLoader> classLoaders;
    private HashMap<String, File> tempFiles;
    private ArrayList<String> started;
    private Injector injector;
    private EventBus moduleEventBus;

    public SimpleModuleManagerOld(ShardManager shardManager, DatabaseManager databaseManager, I18n i18n, CommandManager commandManager, EventWaiter eventWaiter) {
        logger = LoggerFactory.getLogger(getClass());
        this.shardManager = shardManager;
        this.databaseManager = databaseManager;
        this.i18n = i18n;
        this.commandManager = commandManager;
        this.eventWaiter = eventWaiter;

        modules = new HashMap<>();
        classLoaders = new HashMap<>();
        tempFiles = new HashMap<>();
        started = new ArrayList<>();
    }

    @Override public void loadModules() {
        i18n.loadMessages();

        moduleEventBus = new EventBus();
        try {
            new URL("file:///").openConnection().setDefaultUseCaches(false); // disable URL caching - hotswap fix
        } catch (Exception e) { // should not happen
            logger.error("This should not happen!", e);
        }

        if (classLoaders.size() != 0) {
            commandManager.unregisterAll();
            Iterator<Map.Entry<String, URLClassLoader>> in = classLoaders.entrySet().iterator();
            while (in.hasNext()) {
                String name = in.next().getKey();
                stopModule(name);
                unload(name, false);
                modules.remove(name);
                in.remove();
                tempFiles.remove(name);
            }
        }

        if (MODULES_DIR.exists()) {
            try {
                modules.put("core", new CoreModule());
                classLoaders.put("core", null);
                Stream<Path> pathStream = Files.list(MODULES_DIR.toPath());

                pathStream.filter(path -> path.toString().toLowerCase().endsWith(".jar")).forEach(path -> {
                    try {
                        load(path.toAbsolutePath().toString());
                    } catch (Exception e) {
                        logger.error("Error loading module \"" + path + "\"!", e);
                        Sentry.capture(e);
                    }
                });
                pathStream.close();

                injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<KyokoModule> binder = Multibinder.newSetBinder(binder(), KyokoModule.class);
                        for(KyokoModule mod : modules.values()) {
                            binder.addBinding().to(mod.getClass());
                        }
                        if (shardManager != null)
                            bind(ShardManager.class).toInstance(shardManager);
                        bind(I18n.class).toInstance(i18n);
                        bind(DatabaseManager.class).toInstance(databaseManager);
                        bind(CommandManager.class).toInstance(commandManager);
                        bind(ModuleManager.class).toInstance(SimpleModuleManagerOld.this);
                        bind(EventBus.class).toInstance(moduleEventBus);
                        bind(EventWaiter.class).toInstance(eventWaiter);
                    }
                });

                for (String s : modules.keySet()) {
                    startModule(s);
                }
            } catch (IOException e) {
                logger.error("Error while (re)loading modules!", e);
                Sentry.capture(e);
            }
        }
    }

    @Override public void startModule(String name) {
        if (!started.contains(name)) {
            logger.info("Starting module: {}", name);
            try {
                KyokoModule mod = injector.getInstance(modules.get(name).getClass());
                mod.startUp();
                modules.replace(name, mod);
                started.add(name);
            } catch (Exception e) {
                logger.error("Error starting module: " + name, e);
                Sentry.capture(e);
            }
        }
    }

    @Override public void stopModule(String name) {
        if (started.contains(name)) {
            logger.info("Stopping module: {}", name);
            try {
                modules.get(name).shutDown();
            } catch (Exception e) {
                logger.error("Error stopping module: " + name, e);
                Sentry.capture(e);
            }

            Iterator<String> i = started.iterator();
            while (i.hasNext())
                if (i.next().equalsIgnoreCase(name)) i.remove();
        }
    }

    @Override public void unload(String name, boolean remove) {
        if (classLoaders.get(name) == null) return;

        try {
            classLoaders.get(name).close();
            if (tempFiles.get(name) != null)
                Files.delete(tempFiles.get(name).toPath());
        } catch (IOException e) {
            logger.error("Caught error while unloading module!", e);
            Sentry.capture(e);
        }
        if (remove) {
            modules.entrySet().removeIf(e -> e.getKey().equals(name));
            classLoaders.entrySet().removeIf(e -> e.getKey().equals(name));
            tempFiles.entrySet().removeIf(e -> e.getKey().equals(name));
        }
    }

    @Override public void load(String path) throws Exception {
        File jar = new File(path);
        File jar2 = File.createTempFile("cached-", ".kymod");
        jar2.deleteOnExit();

        try (InputStream fis = new FileInputStream(jar)) {
            try (OutputStream fos = new FileOutputStream(jar2)) {
                IOUtils.copy(fis, fos);
            }
        }

        logger.debug("Caching module: {} -> {}", path, jar2.getAbsolutePath());

        URL jarUrl = jar2.toURI().toURL();
        URL[] classPath = new URL[]{jarUrl};
        URLClassLoader cl = new URLClassLoader(classPath, getClass().getClassLoader());

        URL config = cl.getResource("plugin.json");
        if (config == null) config = cl.getResource("/plugin.json");

        if (config == null) throw new FileNotFoundException("Cannot find plugin.json");
        String data = CommonUtil.fromStream(config.openStream());
        ModuleDescription description = GsonUtil.fromJSON(data, ModuleDescription.class);

        if (description.getName() == null) throw new IllegalArgumentException("No module name specified!");
        if (description.getMain() == null) throw new IllegalArgumentException("No main class specified!");

        if (isLoaded(description.getName())) throw new IllegalArgumentException("Module is already loaded!");

        Class jarClass = cl.loadClass(description.getMain());

        if (!KyokoModule.class.isAssignableFrom(jarClass)) throw new IllegalArgumentException("Module main class does not implement KyokoModule!");

        KyokoModule mod = (KyokoModule) jarClass.newInstance();
        modules.put(description.getName(), mod);
        classLoaders.put(description.getName(), cl);
        tempFiles.put(description.getName(), jar2);
    }

    @Override public boolean isLoaded(String name) {
        return modules.keySet().contains(name);
    }

    @Override public boolean isStarted(String name) {
        return started.contains(name);
    }

    @Override public HashMap<String, KyokoModule> getModules() {
        return modules;
    }

    @Override public ArrayList<String> getStarted() {
        return started;
    }

    @Subscribe
    public void onEvent(Object object) {
        if (moduleEventBus != null)
            moduleEventBus.post(object);
    }
}
