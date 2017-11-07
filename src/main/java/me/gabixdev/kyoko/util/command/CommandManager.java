package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.Kyoko;

import java.util.HashMap;
import java.util.HashSet;

public class CommandManager {
    private Kyoko kyoko;

    private long runs;

    private HashSet<Command> commands;
    private HashMap<String, Command> handlers;

    public CommandManager(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.commands = new HashSet<>();
        this.handlers = new HashMap<>();
        this.runs = 0;
    }

    public void registerCommand(Command c) {
        if (c == null) return;

        commands.removeIf(element -> element.equals(c));
        handlers.entrySet().removeIf(entry -> {
            for (String s : c.getAliases())
                if (s.equalsIgnoreCase(entry.getKey()))
                    return true;

            return false;
        });

        commands.add(c);
        for (String alias : c.getAliases()) {
            if (alias == null || alias.isEmpty()) continue;

            handlers.put(alias.toLowerCase(), c);
        }
    }

    public Command getHandler(String label) {
        label = label.toLowerCase();

        if (!handlers.containsKey(label)) return null;
        else {
            return handlers.get(label);
        }
    }

    public long getCommandRunCount() {
        return runs;
    }

    public void incrementRunCount() {
        runs++;
    }

    public HashSet<Command> getCommands() {
        return commands;
    }
}
