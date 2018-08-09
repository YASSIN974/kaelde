package moe.kyokobot.commands.commands.basic;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import org.jetbrains.annotations.NotNull;

public class PrefixesCommand extends Command {
    public PrefixesCommand() {
        name = "prefixes";
        usage  = "";
        category = CommandCategory.BASIC;
    }

    @Override
    public void execute(@NotNull CommandContext context) {

    }
}
