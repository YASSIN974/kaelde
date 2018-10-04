package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class PauseCommand extends MusicCommand {
    private final MusicManager musicManager;

    public PauseCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
        name = "pause";
        usage = "";
        checkChannel = true;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        if (player.getPlayingTrack() != null) {
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
            if (channel == null)
                channel = context.getChannel();

            if (!player.isPaused()) {
                player.setPaused(true);
                channel.sendMessage(MusicIcons.PAUSE + context.getTranslated("music.paused")).queue();
            } else {
                channel.sendMessage(MusicIcons.PAUSE + context.getTranslated("music.notpaused")).queue();
            }
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", MusicIcons.SHRUG));
        }
    }
}
