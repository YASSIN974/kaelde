package moe.kyokobot.bot.manager;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.module.KyokoModuleDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.exception.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class ModuleManager {
    private static final File MODULES_DIR = new File(System.getProperty("kyoko.plugindir", "modules"));

    private Logger logger;
    private CommandManager commandManager;

    private HashMap<String, KyokoModule> modules;
    private HashMap<String, JarClassLoader> classLoaders;
    private ArrayList<String> started;
    private Injector injector;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public ModuleManager(CommandManager commandManager) {
        logger = LoggerFactory.getLogger(getClass());
        this.commandManager = commandManager;

        modules = new HashMap<>();
        classLoaders = new HashMap<>();
        started = new ArrayList<>();
    }

    public void loadModules() {
        if (classLoaders.size() != 0) {
            Iterator<String> in = classLoaders.keySet().iterator();
            while (in.hasNext()) {
                String name = in.next();
                stopModule(name);
                unload(name);
            }
        }
        System.gc();

        if (MODULES_DIR.exists()) {
            try {
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
                        bind(CommandManager.class).toInstance(commandManager);
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
            started.remove(name);
        }
    }

    public void unload(String name) {
        modules.entrySet().removeIf(e -> e.getKey().equals(name));
        Set<String> classes = new HashSet<String>(classLoaders.get(name).getLoadedClasses().keySet());
        classes.forEach(clazz -> classLoaders.get(name).unloadClass(clazz));
        classLoaders.get(name).close();
        classLoaders.entrySet().removeIf(e -> e.getKey().equals(name));
        System.gc();
    }

    public void load(String path) throws Exception {
        JarClassLoader jcl = new JarClassLoader();
        jcl.add(path);

        URL config = jcl.getResource("plugin.json");
        if (config == null) throw new ResourceNotFoundException("Cannot find /plugin.json");
        KyokoModuleDescription description = gson.fromJson(new InputStreamReader(config.openStream()), KyokoModuleDescription.class);

        if (description.moduleName == null) throw new IllegalArgumentException("No module name specified!");
        if (description.mainClass == null) throw new IllegalArgumentException("No main class specified!");

        Class jarClass = jcl.loadClass(description.mainClass);

        if (!KyokoModule.class.isAssignableFrom(jarClass)) throw new IllegalArgumentException("Module main class does not implement KyokoModule!");

        KyokoModule mod = (KyokoModule) jarClass.newInstance();
        modules.put(description.moduleName, mod);
        classLoaders.put(description.moduleName, jcl);
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
