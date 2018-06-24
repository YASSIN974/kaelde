package moe.kyokobot.social;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.social.commands.ClaimCommand;
import moe.kyokobot.social.commands.ProfileCommand;
import moe.kyokobot.social.commands.SendMoneyCommand;
import moe.kyokobot.social.requester.ImageRequester;

import java.util.ArrayList;

public class Module implements KyokoModule {
    @Inject private CommandManager commandManager;
    @Inject private DatabaseManager databaseManager;
    @Inject private EventWaiter eventWaiter;

    private ArrayList<Command> commands;
    private ImageRequester requester;

    public Module() {
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        requester = new ImageRequester(databaseManager);

        commands = new ArrayList<>();

        commands.add(new ProfileCommand(requester));
        commands.add(new ClaimCommand(databaseManager));
        commands.add(new SendMoneyCommand(databaseManager));
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }
}
