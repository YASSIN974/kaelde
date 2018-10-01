package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import org.jetbrains.annotations.NotNull;

public class ResumeCommand extends MusicCommand {
    private final MusicManager musicManager;

    public ResumeCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
        name = "resume";
        usage = "";
        checkChannel = true;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        if (player.getPlayingTrack() != null) {
            if (player.isPaused()) {
                player.setPaused(false);
                context.send(MusicIcons.PLAY + context.getTranslated("music.resumed"));
            } else {
                context.send(MusicIcons.PLAY + context.getTranslated("music.notresumed"));
            }
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", MusicIcons.SHRUG));
        }
    }
}
