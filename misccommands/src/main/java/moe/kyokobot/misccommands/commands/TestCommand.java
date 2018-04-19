package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;

public class TestCommand extends Command {
    public TestCommand() {
        name = "test";
        description = "test.description";
        category = CommandCategory.BASIC;
    }

    @Override
    public void execute(CommandContext context) {
        context.send("DUPA XD");
    }
}
