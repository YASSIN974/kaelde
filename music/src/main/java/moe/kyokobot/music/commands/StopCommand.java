package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.bot.command.CommandIcons.ERROR;
import static moe.kyokobot.music.MusicIcons.SHRUG;
import static moe.kyokobot.music.MusicIcons.STOP;

public class StopCommand extends MusicCommand {
    private final MusicManager musicManager;

    public StopCommand(MusicManager musicManager) {
        name = "stop";
        usage = "";

        this.musicManager = musicManager;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

        if (player.getPlayingTrack() != null) {
            player.stopTrack();
            musicManager.dispose(context.getGuild());

            context.send(STOP + context.getTranslated("music.stopped"));
        } else {
            context.send(ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", SHRUG));
        }
    }
}
