package me.gabixdev.kyoko.command;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class AliasCommand extends Command {
    private final Kyoko kyoko;
    private final String label;
    private final String description;
    private final String usage;
    private final CommandCategory category;
    private final String[] args;
    private final String[] aliases;

    public AliasCommand(Kyoko kyoko, String label, String description, String usage, CommandCategory category, String[] args) {
        this.kyoko = kyoko;
        this.label = label;
        this.aliases = new String[] { label };
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.args = args;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public CommandCategory getCategory() {
        return category;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void handle(Message message, Event event, String[] origargs) throws Throwable {
        kyoko.getCommandManager().getCommand(args[0]).handle(message, event, args);
    }
}
