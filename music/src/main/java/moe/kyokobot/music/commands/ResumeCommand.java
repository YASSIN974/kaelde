package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.TextChannel;
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
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
            if (channel == null)
                channel = context.getChannel();
            if (player.isPaused()) {
                player.setPaused(false);
                channel.sendMessage(MusicIcons.PLAY + context.getTranslated("music.resumed")).queue();
            } else {
                channel.sendMessage(MusicIcons.PLAY + context.getTranslated("music.notresumed")).queue();
            }
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", MusicIcons.SHRUG));
        }
    }
}
