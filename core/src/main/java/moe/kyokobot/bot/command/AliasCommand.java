package moe.kyokobot.bot.command;

import com.google.common.base.Joiner;
import moe.kyokobot.bot.manager.CommandManager;

public class AliasCommand extends Command {
    private CommandManager commandManager;
    private String execute;
    private String[] args;

    public AliasCommand(CommandManager commandManager, String label, String[] aliases, String description, String usage, CommandCategory category, String execute, String[] args) {
        this.commandManager = commandManager;
        this.name = label;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.execute = execute;
        this.args = args;
    }

    @Override
    public void execute(CommandContext context) {
        Command c = commandManager.getCommands().get(execute);
        if (c != null) {
            CommandContext con = new CommandContext(context.getI18n(), c, context.getEvent(), context.getPrefix(), name, Joiner.on(" ").join(args), args);
            c.execute(con);
        }
    }
}
