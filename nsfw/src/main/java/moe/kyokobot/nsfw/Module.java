package moe.kyokobot.nsfw;

import com.google.gson.Gson;
import com.google.inject.Inject;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.nsfw.commands.LewdNekoCommand;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class Module implements KyokoModule {
    private Logger logger;
    @Inject
    private CommandManager commandManager;
    @Inject
    private Settings settings;
    private ArrayList<Command> commands;
    private HashMap<Guild, Long> cooldowns;

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
        cooldowns = new HashMap<>();
    }

    @Override
    public void startUp() {
        commands.add(new LewdNekoCommand());
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }

}
