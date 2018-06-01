package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.music.MusicManager;

public class SkipCommand extends MusicCommand {
    public SkipCommand(MusicManager manager) {
        name = "skip";
        description = "music.skip.description";
    }

    @Override
    public void execute(CommandContext context) {

    }
}
