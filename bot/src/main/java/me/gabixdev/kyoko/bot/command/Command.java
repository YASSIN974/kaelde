package me.gabixdev.kyoko.bot.command;

public abstract class Command {
    protected String name;
    protected String[] aliases;
    protected String description;
    protected CommandCategory category = CommandCategory.BASIC;
    protected CommandType type = CommandType.NORMAL;

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
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

    }
}
