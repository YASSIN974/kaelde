package moe.kyokobot.bot.manager;

import moe.kyokobot.bot.module.KyokoModule;

import java.util.ArrayList;
import java.util.HashMap;

public interface ModuleManager {
    void loadModules();

    void startModule(String name);

    void stopModule(String name);

    void unload(String name, boolean remove);

    void load(String path) throws Exception;

    boolean isLoaded(String name);

    boolean isStarted(String name);

    HashMap<String, KyokoModule> getModules();

    ArrayList<String> getStarted();
}
