package moe.kyokobot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import moe.kyokobot.music.MusicUtil;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static moe.kyokobot.music.MusicIcons.STOP;

public class SkipCommand extends MusicCommand {
    private final MusicManager musicManager;

    public SkipCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        name = "skip";
        description = "music.skip.description";
        aliases = new String[] {">>", "forceskip"};
    }

    @Override
    public void execute(CommandContext context) {
        // TODO voteskip

        VoiceChannel voiceChannel = MusicUtil.getCurrentChannel(context.getGuild(), context.getMember());
        if (voiceChannel != null) {
            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            MusicQueue queue = musicManager.getQueue(context.getGuild());

            if (queue.isEmpty()) {
                if (player.getPlayingTrack() != null) {
                    context.send(STOP + context.getTranslated("music.stopped"));
                    musicManager.clean(context.getGuild());
                } else {
                    context.send(CommandIcons.error + context.getTranslated("music.queueempty").replace("{prefix}", context.getPrefix()));
                    musicManager.clean(context.getGuild());
                }
            } else {
                // TODO: enable/disable announcing
                queue.setAnnouncing(context.getChannel(), context);

                AudioTrack track = queue.poll();
                player.playTrack(track);
                queue.announce(track);
            }
        } else {
            context.send(CommandIcons.error + context.getTranslated("music.joinchannel"));
        }
    }
}
