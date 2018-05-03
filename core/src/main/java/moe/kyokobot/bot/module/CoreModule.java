package moe.kyokobot.bot.module;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.debug.AvatarCommand;
import moe.kyokobot.bot.command.debug.ModulesCommand;
import moe.kyokobot.bot.command.debug.NameCommand;
import moe.kyokobot.bot.command.debug.ShellCommand;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;

public class CoreModule implements KyokoModule {
    @Inject
    private CommandManager commandManager;
    @Inject
    private ModuleManager moduleManager;

    @Override
    public void startUp() {
        commandManager.registerCommand(new AvatarCommand());
        commandManager.registerCommand(new NameCommand());
        commandManager.registerCommand(new ShellCommand());
        commandManager.registerCommand(new ModulesCommand(moduleManager));
    }

    @Override
    public void shutDown() {

    }
}
