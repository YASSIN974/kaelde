package moe.kyokobot.nsfw;

import com.google.inject.Inject;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.nsfw.commands.BoobsCommand;
import moe.kyokobot.nsfw.commands.LewdNekoCommand;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;

public class Module implements KyokoModule {
    @Inject
    private CommandManager commandManager;
    private ArrayList<Command> commands;
    private HashMap<Guild, Long> cooldowns;

    public Module() {
        commands = new ArrayList<>();
        cooldowns = new HashMap<>();
    }

    @Override
    public void startUp() {
        commands = new ArrayList<>();

        commands.add(new LewdNekoCommand());
        commands.add(new BoobsCommand());
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        commands.forEach(commandManager::unregisterCommand);
    }

}
