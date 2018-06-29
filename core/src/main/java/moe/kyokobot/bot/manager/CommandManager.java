package moe.kyokobot.bot.manager;

import moe.kyokobot.bot.command.Command;

import java.util.Map;
import java.util.Set;

public interface CommandManager {
    Set<Command> getRegistered();

    Map<String, Command> getCommands();

    void registerCommand(Command command);

    void unregisterCommand(Command command);

    void unregisterAll();
}
