package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import org.jetbrains.annotations.NotNull;

public class PauseCommand extends MusicCommand {
    private final MusicManager musicManager;

    public PauseCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
        name = "pause";
        usage = "";
        description = "music.pause.description";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        if (player.getPlayingTrack() != null) {
            if (!player.isPaused()) {
                player.setPaused(true);
                context.send(MusicIcons.PAUSE + context.getTranslated("music.paused"));
            } else {
                context.send(MusicIcons.PAUSE + context.getTranslated("music.notpaused"));
            }
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", MusicIcons.SHRUG));
        }
    }
}
