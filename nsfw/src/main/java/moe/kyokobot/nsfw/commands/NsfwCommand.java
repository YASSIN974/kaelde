package moe.kyokobot.nsfw.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;

public class NsfwCommand extends Command {
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.NSFW;
    }

    @Override
    public void preExecute(CommandContext context) {
        if (context.getChannel().isNSFW()) {
            super.preExecute(context);
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("generic.nsfw"));
        }
    }
}
