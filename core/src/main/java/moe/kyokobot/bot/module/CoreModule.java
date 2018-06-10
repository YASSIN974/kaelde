package moe.kyokobot.bot.module;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.debug.*;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;

public class CoreModule implements KyokoModule {
    @Inject
    private CommandManager commandManager;
    @Inject
    private ModuleManager moduleManager;
    @Inject
    private DatabaseManager databaseManager;

    @Override
    public void startUp() {
        commandManager.registerCommand(new UpdateAvatarCommand());
        commandManager.registerCommand(new EvalCommand(moduleManager, commandManager, databaseManager));
        commandManager.registerCommand(new NameCommand());
        commandManager.registerCommand(new ShellCommand());
        commandManager.registerCommand(new ModulesCommand(moduleManager));
    }

    @Override
    public void shutDown() {

    }
}
