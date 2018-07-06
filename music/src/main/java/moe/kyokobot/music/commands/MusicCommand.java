package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;

public abstract class MusicCommand extends Command {
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public String getUsage() {
        return usage == null ? "music." + name + ".usage" : usage;
    }

    @Override
    public String getDescription() {
        return description == null ? "music." + name + ".description" : description;
    }

    @Override
    public void preExecute(CommandContext context) {
        super.preExecute(context);
    }
}
