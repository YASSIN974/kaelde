package moe.kyokobot.misccommands.commands.basic;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;

public class AboutCommand extends Command {
    public AboutCommand() {
        name = "about";
        aliases = new String[] {"botinfo", "stats"};
    }

    @Override
    public void execute(CommandContext context) {

    }
}
