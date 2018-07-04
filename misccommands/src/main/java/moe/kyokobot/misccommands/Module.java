package moe.kyokobot.misccommands;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.misccommands.commands.RandomTextCommand;
import moe.kyokobot.misccommands.commands.SimpleTextCommand;
import moe.kyokobot.misccommands.commands.basic.*;
import moe.kyokobot.misccommands.commands.fun.*;
import moe.kyokobot.misccommands.commands.images.CoffeeCommand;
import moe.kyokobot.misccommands.handler.AutoRoleHandler;

import java.util.ArrayList;

public class Module implements KyokoModule {

    @Inject
    private CommandManager commandManager;
    @Inject
    private DatabaseManager databaseManager;
    @Inject
    private EventBus eventBus;
    private ArrayList<Command> commands;
    private AutoRoleHandler autoRoleHandler;

    public Module() {
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        commands = new ArrayList<>();

        autoRoleHandler = new AutoRoleHandler(databaseManager);

        commands.add(new HelpCommand(commandManager));
        commands.add(new StatsCommand(commandManager));
        commands.add(new PingCommand());
        commands.add(new UserInfoCommand());
        commands.add(new ServerInfoCommand());

        commands.add(new BananaCommand());
        commands.add(new WhyCommand());
        commands.add(new OwOifyCommand());
        commands.add(new InspireCommand());
        commands.add(new EmojiCommand());
        commands.add(new SnowflakeCommand());
        commands.add(new SimpleTextCommand("lenny", "( ͡° ͜ʖ ͡°)"));
        commands.add(new SimpleTextCommand("shrug", "¯\\_(ツ)_/¯"));
        commands.add(new RandomTextCommand("tableflip", new String[] {" (╯°□°）╯︵ ┻━┻", "(┛◉Д◉)┛彡┻━┻", "(ﾉ≧∇≦)ﾉ ﾐ ┸━┸", "(ノಠ益ಠ)ノ彡┻━┻", "(╯ರ ~ ರ）╯︵ ┻━┻", "(┛ಸ_ಸ)┛彡┻━┻", "(ﾉ´･ω･)ﾉ ﾐ ┸━┸", "(ノಥ,_｣ಥ)ノ彡┻━┻", "(┛✧Д✧))┛彡┻━┻"}));
        commands.add(new SnipeCommand(eventBus));
        commands.add(new SayCommand());
        commands.add(new AvatarCommand());

        commands.add(new CoffeeCommand());


        if (getClass().getResource("/commit_messages.txt") != null)
            commands.add(new WhatTheCommitCommand());

        commands.forEach(commandManager::registerCommand);

        eventBus.register(autoRoleHandler);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);

        try {
            eventBus.unregister(autoRoleHandler);
        } catch (Exception ignored) {
            // ignored
        }
    }
}
