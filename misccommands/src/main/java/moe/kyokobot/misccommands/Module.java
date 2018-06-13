package moe.kyokobot.misccommands;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.misccommands.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Module implements KyokoModule {
    private Logger logger;
    @Inject
    private CommandManager commandManager;
    @Inject
    private EventWaiter eventWaiter;
    @Inject
    private EventBus eventBus;
    private ArrayList<Command> commands;

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        commands.add(new HelpCommand(commandManager));

        commands.add(new CoffeeCommand());

        commands.add(new PingCommand(commandManager));
        commands.add(new SayCommand(commandManager));
        commands.add(new AvatarCommand(commandManager));
        commands.add(new UserInfoCommand(commandManager));
        commands.add(new ServerInfoCommand(commandManager));

        commands.add(new WhyCommand());
        commands.add(new OwOifyCommand());
        commands.add(new SimpleTextCommand("lenny", "( ͡° ͜ʖ ͡°)"));
        commands.add(new SimpleTextCommand("shrug", "¯\\_(ツ)_/¯"));
        commands.add(new RandomTextCommand("tableflip", new String[] {" (╯°□°）╯︵ ┻━┻", "(┛◉Д◉)┛彡┻━┻", "(ﾉ≧∇≦)ﾉ ﾐ ┸━┸", "(ノಠ益ಠ)ノ彡┻━┻", "(╯ರ ~ ರ）╯︵ ┻━┻", "(┛ಸ_ಸ)┛彡┻━┻", "(ﾉ´･ω･)ﾉ ﾐ ┸━┸", "(ノಥ,_｣ಥ)ノ彡┻━┻", "(┛✧Д✧))┛彡┻━┻"}));
        commands.add(new SnipeCommand(eventBus));

        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }
}
