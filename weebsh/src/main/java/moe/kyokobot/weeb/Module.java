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
import moe.kyokobot.weeb.commands.ActionCommand;
import moe.kyokobot.weeb.commands.WeebCommand;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Module implements KyokoModule {
    private Logger logger;
    @Inject
    private CommandManager commandManager;
    private ArrayList<Command> commands;
    private HashMap<Guild, Long> cooldowns;

    private Weeb4J weeb4J;

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
        cooldowns = new HashMap<>();
    }

    @Override
    public void startUp() {
        Settings settings = Settings.instance;
        if (settings.apiKeys.containsKey("weebsh")) {
            weeb4J = new Weeb4J.Builder().setBotInfo("Kyoko", Constants.VERSION).setToken(TokenType.WOLKE, settings.apiKeys.get("weebsh")).build();
            commands = new ArrayList<>();

            commands.add(new WeebCommand(weeb4J));
            commands.add(new ActionCommand(weeb4J, cooldowns, "hug"));
            commands.add(new ActionCommand(weeb4J, cooldowns, "pat"));
            commands.add(new ActionCommand(weeb4J, cooldowns, "slap"));
            commands.add(new ActionCommand(weeb4J, cooldowns, "kiss"));
            commands.add(new ActionCommand(weeb4J, cooldowns, "lick"));
            commands.add(new ActionCommand(weeb4J, cooldowns, "punch"));
            commands.add(new ActionCommand(weeb4J, cooldowns, "tickle"));
            commands.addAll(createWeebCommandAliases(commandManager, "awoo", "blush", "clagwimoth", "cry", "dance", "jojo", "lewd", "megumin", "kemonomimi", "cat", "discordmeme", "initiald"));
        } else {
            logger.warn("No weebsh token set in config!");
        }
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }

    public List<Command> createWeebCommandAliases(CommandManager commandManager, String... commands) {
        List<Command> cmds = new ArrayList<>();
        for (String name : commands) {
            String srcname = name;

            switch (name) {
                case "cat":
                    srcname = "animal_cat";
                    break;
                case "discordmeme":
                    srcname = "discord_memes";
                    break;
                case "initiald":
                    srcname = "initial_d";
                    break;
            }

            cmds.add(new AliasCommand(commandManager, name, new String[0], "weebsh.description." + name, null, CommandCategory.IMAGES, "weeb", new String[]{srcname}));
        }
        return cmds;
    }
}
