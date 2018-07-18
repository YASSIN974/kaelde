package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import org.jetbrains.annotations.NotNull;

public class ShuffleCommand extends MusicCommand {

    private final MusicManager musicManager;

    public ShuffleCommand(MusicManager musicManager) {
        name = "shuffle";
        usage = "";

        this.musicManager = musicManager;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        MusicQueue queue = musicManager.getQueue(context.getGuild());
        if (player.getPlayingTrack() != null) {
            queue.shuffle();
            context.send(MusicIcons.PLAY + context.getTranslated("music.shuffled"));
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", MusicIcons.SHRUG));
        }
    }
}
