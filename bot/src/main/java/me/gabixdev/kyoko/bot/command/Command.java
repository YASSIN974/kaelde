package me.gabixdev.kyoko.bot.command;

public abstract class Command {
    protected String name;
    protected String[] aliases = new String[0];
    protected String usage;
    protected String description;
    protected CommandCategory category = CommandCategory.BASIC;
    protected CommandType type = CommandType.NORMAL;

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public CommandType getType() {
        return type;
    }

    public void execute(CommandContext context) {
        throw new UnsupportedOperationException("Command has not implemented execute()");
    }
}
