package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;

public class PauseCommand extends MusicCommand {
    private final MusicManager musicManager;

    public PauseCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
        name = "pause";
        description = "music.pause.description";
    }

    @Override
    public void execute(CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        if (player.getPlayingTrack() != null) {
            player.setPaused(true);
        } else {
            context.send(CommandIcons.error + context.getTranslated(""));
        }
    }
}
