package moe.kyokobot.moderation;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.moderation.commands.*;
import moe.kyokobot.moderation.handler.InviteHandler;

import java.util.ArrayList;

public class Module implements KyokoModule {

    @Inject
    private CommandManager commandManager;
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private EventWaiter eventWaiter;
    @Inject
    private EventBus eventBus;

    private ArrayList<Command> commands;

    private InviteHandler inviteHandler;

    public Module() {
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        commands = new ArrayList<>();

        inviteHandler = new InviteHandler(databaseManager);

        eventBus.register(inviteHandler);

        commands.add(new SettingsCommand(eventWaiter, databaseManager));
        commands.add(new KickCommand());
        commands.add(new BanCommand());
        commands.add(new UnbanCommand());
        commands.add(new PruneCommand());
        commands.add(new VoiceKickCommand());

        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }
}
