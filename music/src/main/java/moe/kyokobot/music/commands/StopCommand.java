package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.bot.command.CommandIcons.ERROR;
import static moe.kyokobot.music.MusicIcons.SHRUG;
import static moe.kyokobot.music.MusicIcons.STOP;

public class StopCommand extends MusicCommand {
    private final MusicManager musicManager;

    public StopCommand(MusicManager musicManager) {
        name = "stop";
        usage = "";
        checkChannel = true;
        this.musicManager = musicManager;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

        if (player.getPlayingTrack() != null) {
            player.stopTrack();
            musicManager.dispose(context.getGuild());
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
            if (channel == null)
                channel = context.getChannel();
            channel.sendMessage(STOP + context.getTranslated("music.stopped")).queue();
        } else {
            context.send(ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", SHRUG));
        }
    }
}
