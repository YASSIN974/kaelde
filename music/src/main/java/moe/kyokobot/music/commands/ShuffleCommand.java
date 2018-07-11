package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.music.MusicManager;

public class ShuffleCommand extends MusicCommand {

    private final MusicManager musicManager;

    public ShuffleCommand(MusicManager musicManager) {
        name = "shuffle";

        this.musicManager = musicManager;
    }

    @Override
    public void execute(CommandContext context) {
        
    }
}
