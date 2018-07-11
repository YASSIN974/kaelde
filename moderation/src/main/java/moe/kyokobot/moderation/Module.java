package moe.kyokobot.moderation;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.moderation.commands.SettingsCommand;

import java.util.ArrayList;

public class Module implements KyokoModule {

    @Inject
    private CommandManager commandManager;
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private EventWaiter eventWaiter;

    private ArrayList<Command> commands;

    public Module() {
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        commands = new ArrayList<>();

        commands.add(new SettingsCommand(eventWaiter, databaseManager));

        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }
}
