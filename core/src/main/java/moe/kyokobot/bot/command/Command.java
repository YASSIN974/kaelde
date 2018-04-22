package moe.kyokobot.bot.command;

public abstract class Command {
    protected String name;
    protected String[] aliases = new String[0];
    protected String usage;
    protected String description;
    protected CommandCategory category = null;
    protected CommandType type = CommandType.NORMAL;
    protected boolean allowInDMs = false;

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

    public boolean isAllowInDMs() {
        return allowInDMs;
    }

    public void execute(CommandContext context) {
        throw new UnsupportedOperationException("Command has not implemented execute()");
    }
}
