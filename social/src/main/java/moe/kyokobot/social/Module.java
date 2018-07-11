package moe.kyokobot.social;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.social.commands.ClaimCommand;
import moe.kyokobot.social.commands.ProfileCommand;
import moe.kyokobot.social.commands.RichestCommand;
import moe.kyokobot.social.commands.SendMoneyCommand;
import moe.kyokobot.social.requester.ImageRequester;

import java.util.ArrayList;

public class Module implements KyokoModule {
    @Inject private CommandManager commandManager;
    @Inject private DatabaseManager databaseManager;
    @Inject private EventWaiter eventWaiter;
    @Inject private EventBus eventBus;
    @Inject private I18n i18n;

    private ArrayList<Command> commands;
    private ImageRequester requester;
    private LevelHandler levelHandler;

    public Module() {
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        requester = new ImageRequester(databaseManager);
        levelHandler = new LevelHandler(databaseManager, i18n);

        commands = new ArrayList<>();

        commands.add(new ProfileCommand(requester));
        commands.add(new ClaimCommand(databaseManager));
        commands.add(new SendMoneyCommand(databaseManager));
        commands.add(new RichestCommand(databaseManager));

        commands.forEach(commandManager::registerCommand);
        eventBus.register(levelHandler);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);

        try {
            eventBus.unregister(levelHandler);
        } catch (Exception ignored) {
            // ignore cuz eventbus is gay
        }
    }
}
