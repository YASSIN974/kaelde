package moe.kyokobot.misccommands;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.misccommands.commands.TestCommand;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.manager.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Module implements KyokoModule {
    private Logger logger;
    @Inject
    private CommandManager commandManager;
    private ArrayList<Command> commands;

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        logger.info("Hello from miscommands XD");
        commands.add(new TestCommand());

        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }
}
