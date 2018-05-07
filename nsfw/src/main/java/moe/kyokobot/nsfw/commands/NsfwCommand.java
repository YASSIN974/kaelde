package moe.kyokobot.nsfw.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;

public class NsfwCommand extends Command {
    @Override
    public void preExecute(CommandContext context) {
        if (context.getChannel().isNSFW()) {
            super.preExecute(context);
        } else {
            context.send(context.error() + context.getTranslated("generic.nsfw"));
        }
    }
}
