package moe.kyokobot.bot.module;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.debug.ModulesCommand;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;

public class CoreModule implements KyokoModule {
    @Inject
    private CommandManager commandManager;
    @Inject
    private ModuleManager moduleManager;

    @Override
    public void startUp() {
        commandManager.registerCommand(new ModulesCommand(moduleManager));
    }

    @Override
    public void shutDown() {

    }
}
