package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;

public class SimpleTextCommand extends Command {
    private final String text;

    public SimpleTextCommand(String name, String text) {
        this.text = text;
        this.name = name;
        this.description = text;
        this.category = CommandCategory.FUN;
    }

    @Override
    public void execute(CommandContext context) {
        context.send(text);
    }
}
