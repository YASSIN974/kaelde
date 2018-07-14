package moe.kyokobot.bot.module;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.debug.*;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;
import net.dv8tion.jda.bot.sharding.ShardManager;

public class CoreModule implements KyokoModule {
    @Inject
    private ShardManager shardManager;
    @Inject
    private CommandManager commandManager;
    @Inject
    private ModuleManager moduleManager;
    @Inject
    private DatabaseManager databaseManager;

    @Override
    public void startUp() {
        commandManager.registerCommand(new UpdateAvatarCommand());
        commandManager.registerCommand(new EvalCommand(shardManager, moduleManager, commandManager, databaseManager));
        commandManager.registerCommand(new SetNameCommand());
        commandManager.registerCommand(new ShellCommand());
        commandManager.registerCommand(new CleanSelfCommand());
        commandManager.registerCommand(new ModulesCommand(moduleManager));
        commandManager.registerCommand(new ReloadSettingsCommand());
        commandManager.registerCommand(new ReloadMessagesCommand());
        commandManager.registerCommand(new GenDocsCommand(commandManager));
    }

    @Override
    public void shutDown() {

    }
}
