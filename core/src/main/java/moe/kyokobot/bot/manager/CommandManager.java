package moe.kyokobot.bot.manager;

import moe.kyokobot.bot.command.Command;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Map;
import java.util.Set;

public interface CommandManager {
    Set<Command> getRegistered();

    Map<String, Command> getCommands();

    void registerCommand(Command command);

    void unregisterCommand(Command command);

    void unregisterAll();

    boolean isExperimental(Guild guild);

    long getRuns();
}
