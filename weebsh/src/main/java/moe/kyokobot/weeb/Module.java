package moe.kyokobot.weeb;

import com.github.natanbc.weeb4j.TokenType;
import com.github.natanbc.weeb4j.Weeb4J;
import com.google.inject.Inject;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.AliasCommand;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.weeb.commands.WeebCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Module implements KyokoModule {
    private Logger logger;
    @Inject
    private CommandManager commandManager;
    @Inject
    private Settings settings;
    private ArrayList<Command> commands;

    private Weeb4J weeb4J;

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        if (settings.apiKeys.containsKey("weebsh")) {
            weeb4J = new Weeb4J.Builder().setBotInfo("Kyoko", Constants.VERSION).setToken(TokenType.WOLKE, settings.apiKeys.get("weebsh")).build();

            commands.add(new WeebCommand(weeb4J));
            commands.add(new AliasCommand(commandManager, "trap", new String[0], "trap.description", null, CommandCategory.IMAGES, "weeb", new String[]{"trap"}));
        } else {
            logger.warn("No weebsh token set in config!");
        }
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }
}
