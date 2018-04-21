package moe.kyokobot.bot.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.module.CoreModule;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.module.KyokoModuleDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ModuleManager {
    private static final File MODULES_DIR = new File(System.getProperty("kyoko.plugindir", "modules"));

    private Logger logger;
    private Settings settings;
    private CommandManager commandManager;

    private HashMap<String, KyokoModule> modules;
    private HashMap<String, URLClassLoader> classLoaders;
    private ArrayList<String> started;
    private Injector injector;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public ModuleManager(Settings settings, CommandManager commandManager) {
        logger = LoggerFactory.getLogger(getClass());
        this.settings = settings;
        this.commandManager = commandManager;

        modules = new HashMap<>();
        classLoaders = new HashMap<>();
        started = new ArrayList<>();
    }

    public void loadModules() {
        try {
            new URL("http://localhost/").openConnection().setDefaultUseCaches(false); // disable URL caching - hotswap fix
        } catch (Exception e) { // should not happen
            e.printStackTrace();
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
            }
        }
        System.gc();

        if (MODULES_DIR.exists()) {
            try {
                modules.put("core", new CoreModule());
                classLoaders.put("core", null);
                Files.list(MODULES_DIR.toPath()).filter(path -> path.toString().toLowerCase().endsWith(".jar")).forEach(path -> {
                    try {
                        load(path.toAbsolutePath().toString());
                    } catch (Exception e) {
                        logger.error("Error loading module \"" + path + "\"!");
                        e.printStackTrace();
                    }
                });

                injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<KyokoModule> multibinder = Multibinder.newSetBinder(binder(), KyokoModule.class);
                        for(KyokoModule mod : modules.values()) {
                            multibinder.addBinding().to(mod.getClass());
                        }
                        bind(Settings.class).toInstance(settings);
                        bind(CommandManager.class).toInstance(commandManager);
                        bind(ModuleManager.class).toInstance(ModuleManager.this);
                    }
                });

                for (String s : modules.keySet()) {
                    startModule(s);
                }
            } catch (IOException e) {
                logger.error("Error while (re)loading modules!");
                e.printStackTrace();
            }
        }
    }

    public void startModule(String name) {
        if (!started.contains(name)) {
            logger.info("Starting module: " + name);
            try {
                KyokoModule mod = injector.getInstance(modules.get(name).getClass());
                mod.startUp();
                modules.replace(name, mod);
                started.add(name);
                System.gc();
            } catch (Exception e) {
                logger.error("Error starting module: " + name);
                e.printStackTrace();
            }
        }
    }

    public void stopModule(String name) {
        if (started.contains(name)) {
            logger.info("Stopping module: " + name);
            try {
                modules.get(name).shutDown();
            } catch (Exception e) {
                logger.error("Error stopping module: " + name);
                e.printStackTrace();
            }

            Iterator<String> i = started.iterator();
            while (i.hasNext())
                if (i.next().equalsIgnoreCase(name)) i.remove();
        }
    }

    public void unload(String name, boolean remove) {
        if (classLoaders.get(name) == null) return;

        try {
            classLoaders.get(name).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (remove) {
            modules.entrySet().removeIf(e -> e.getKey().equals(name));
            classLoaders.entrySet().removeIf(e -> e.getKey().equals(name));
        }
        System.gc();
    }

    public void load(String path) throws Exception {
        File jar = new File(path);
        URL jarUrl = jar.toURI().toURL();
        URL[] classPath = new URL[]{jarUrl};
        URLClassLoader cl = new URLClassLoader(classPath, getClass().getClassLoader());

        URL config = cl.getResource("plugin.json");
        if (config == null) config = cl.getResource("/plugin.json");

        if (config == null) throw new FileNotFoundException("Cannot find plugin.json");
        KyokoModuleDescription description = gson.fromJson(new InputStreamReader(config.openStream()), KyokoModuleDescription.class);

        if (description.moduleName == null) throw new IllegalArgumentException("No module name specified!");
        if (description.mainClass == null) throw new IllegalArgumentException("No main class specified!");

        Class jarClass = cl.loadClass(description.mainClass);

        if (!KyokoModule.class.isAssignableFrom(jarClass)) throw new IllegalArgumentException("Module main class does not implement KyokoModule!");

        KyokoModule mod = (KyokoModule) jarClass.newInstance();
        modules.put(description.moduleName, mod);
        classLoaders.put(description.moduleName, cl);
    }

    public boolean isLoaded(String name) {
        return modules.keySet().contains(name);
    }

    public boolean isStarted(String name) {
        return started.contains(name);
    }

    public HashMap<String, KyokoModule> getModules() {
        return modules;
    }

    public ArrayList<String> getStarted() {
        return started;
    }
}
