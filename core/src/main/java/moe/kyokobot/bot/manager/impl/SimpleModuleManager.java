package moe.kyokobot.bot.manager.impl;

import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import io.sentry.Sentry;
import lombok.Getter;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.module.ModuleDescription;
import moe.kyokobot.bot.util.CommonUtil;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.GsonUtil;
import moe.kyokobot.bot.util.graph.Graph;
import net.dv8tion.jda.bot.sharding.ShardManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SimpleModuleManager implements ModuleManager {
    private static final File MODULES_DIR = new File(System.getProperty("kyoko.plugindir", "modules"));

    private final ShardManager shardManager;
    private final DatabaseManager databaseManager;
    private final I18n i18n;
    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;
    private final Logger logger;
    @Getter
    private HashMap<String, KyokoModule> modules;
    private HashMap<String, URLClassLoader> classLoaders;
    private HashMap<String, File> tempFiles;
    @Getter
    private ArrayList<String> started;
    private Injector injector;
    private EventBus moduleEventBus;

    LinkedHashSet<String> loadOrder = null;
    Map<String, File> modNames = null;
    Map<String, Collection<String>> dependencies = null;
    Graph<String> graph = null;

    public SimpleModuleManager(ShardManager shardManager, DatabaseManager databaseManager, I18n i18n, CommandManager commandManager, EventWaiter eventWaiter) {
        logger = LoggerFactory.getLogger(getClass());
        this.shardManager = shardManager;
        this.databaseManager = databaseManager;
        this.i18n = i18n;
        this.commandManager = commandManager;
        this.eventWaiter = eventWaiter;

        modules = new LinkedHashMap<>();
        classLoaders = new HashMap<>();
        tempFiles = new HashMap<>();
        started = new ArrayList<>();
    }

    @Override
    public void loadModules() {
        i18n.loadMessages();

        moduleEventBus = new EventBus();
        try {
            new URL("file:///").openConnection().setDefaultUseCaches(false); // disable URL caching - hotswap fix
        } catch (Exception e) { // should not happen
            logger.error("This should not happen!", e);
        }

        if (!classLoaders.isEmpty()) {
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
            try (Stream<Path> pathStream = Files.list(MODULES_DIR.toPath())) {
                LinkedList<String> dependOrder = new LinkedList<>();
                loadOrder = new LinkedHashSet<>();
                modNames = new HashMap<>();
                dependencies = new HashMap<>();

                graph = new Graph<>(dependOrder::add);

                List<File> modFiles = pathStream.filter(path -> path.toString().toLowerCase().endsWith(".jar"))
                        .map(Path::toFile).collect(Collectors.toList());

                modFiles.forEach(this::addDependencies);

                graph.generateDependencies();
                Collections.reverse(dependOrder);
                loadOrder.addAll(dependOrder);

                checkDependencies();
                loadJars();

                injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<KyokoModule> binder = Multibinder.newSetBinder(binder(), KyokoModule.class);
                        for(KyokoModule mod : modules.values()) {
                            binder.addBinding().to(mod.getClass());
                        }
                        bind(ShardManager.class).toInstance(shardManager);
                        bind(I18n.class).toInstance(i18n);
                        bind(DatabaseManager.class).toInstance(databaseManager);
                        bind(CommandManager.class).toInstance(commandManager);
                        bind(ModuleManager.class).toInstance(SimpleModuleManager.this);
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

    private void addDependencies(File file) {
        if (!file.exists() || !file.isFile()) return;
        ModuleDescription desc = getDescription(file);
        if (desc != null) {
            if (desc.getName() == null || desc.getMain() == null) {
                logger.warn("Invalid module description file!");
                return;
            }

            if (modNames.containsKey(desc.getName())) {
                logger.warn("Ambiguous module name {}, skipping!", desc.getName());
                return;
            }

            modNames.put(desc.getName(), file);
            dependencies.put(desc.getName(), desc.getDependencies());

            if (desc.getDependencies() != null) {
                desc.getDependencies().forEach(dep -> {
                    logger.debug("Module {} depends on {}", desc.getName(), dep);
                    graph.addDependency(desc.getName(), dep);
                });
            } else {
                logger.debug("Module {} has no dependencies", desc.getName());
                loadOrder.add(desc.getName());
            }
        }

    }

    @SuppressWarnings("squid:S3776")
    private void checkDependencies() {
        List<String> toRemove = new ArrayList<>();

        for (String name : loadOrder) {
            if (!modNames.containsKey(name)) {
                toRemove.add(name);

                dependencies.forEach((modName, modDeps) -> {
                    if (modDeps != null) {
                        if (modDeps.contains(name))
                            toRemove.add(modName);

                        modDeps.forEach(dep -> {
                            if (toRemove.contains(dep))
                                toRemove.add(modName);
                        });
                    }
                });

                logger.error("Unresolved dependency {}, skipping!", name);
            }
        }

        if (!toRemove.isEmpty())
            loadOrder.removeIf(toRemove::contains);
    }

    private ModuleDescription getDescription(File file) {
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals("plugin.json")) {
                    String data = CommonUtil.fromStream(zipFile.getInputStream(entry));

                    return GsonUtil.fromJSON(data, ModuleDescription.class);
                }
            }
        } catch (Exception e) {
            logger.error("Error while reading description!", e);
        }
        return null;
    }

    private void loadJars() {
        logger.debug("Module load order: {}", Joiner.on(" <- ").join(loadOrder));

        for (String module : loadOrder) {
            try {
                load(modNames.get(module).getAbsolutePath());
            } catch (Exception e) {
                logger.error("Error loading module {}!", module, e);
            }
        }
    }

    @Override
    public void startModule(String name) {
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

    @Override
    public void stopModule(String name) {
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

    @Override
    public void unload(String name, boolean remove) {
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

    @Override
    public void load(String path) throws Exception {
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

        ModuleDescription description = getDescription(jar2);

        if (description == null)
            throw new IllegalArgumentException("Specified module contains invalid description file!");

        String name = description.getName();
        String main = description.getMain();

        if (name == null) throw new IllegalArgumentException("No module name specified!");
        if (main == null) throw new IllegalArgumentException("No main class specified!");

        if (description.getDependencies() != null)
            for (String s : description.getDependencies()) {
                if (!isLoaded(s))
                    throw new IllegalStateException("Module " + name + " depends on " + s + " which is not loaded!");
            }

        if (isLoaded(name))
            throw new IllegalArgumentException("Module is already loaded!");

        Class jarClass = cl.loadClass(main);

        if (!KyokoModule.class.isAssignableFrom(jarClass))
            throw new IllegalArgumentException("Module main class does not implement KyokoModule!");

        KyokoModule mod = (KyokoModule) jarClass.newInstance();
        modules.put(name, mod);
        classLoaders.put(name, cl);
        tempFiles.put(name, jar2);
    }

    @Override
    public boolean isLoaded(String name) {
        return modules.keySet().contains(name);
    }

    @Override
    public boolean isStarted(String name) {
        return started.contains(name);
    }

    @Subscribe
    public void onEvent(Object object) {
        if (moduleEventBus != null)
            moduleEventBus.post(object);
    }
}
