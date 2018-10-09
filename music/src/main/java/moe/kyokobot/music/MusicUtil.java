package moe.kyokobot.music;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandContext;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static moe.kyokobot.bot.command.CommandIcons.ERROR;

public class MusicUtil {
    public static Cache<Guild, Boolean> locks = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(2000).build();

    public static boolean isChannelEmpty(Guild guild, VoiceChannel channel) {
        if (guild == null || channel == null) return true;
        return channel.getMembers().stream().allMatch(member -> member.getUser().isBot());
    }

    public static void play(MusicManager musicManager, MusicPlayer player, MusicQueue queue, CommandContext context, VoiceChannel voiceChannel) {
        if (player.getPlayingTrack() == null) {
            int timeout = 0;

            try {
                musicManager.openConnection(context.getGuild(), voiceChannel);
            } catch (PermissionException e) {
                locks.invalidate(context.getGuild());
                return;
            }

            while (!player.isConnected()) {
                if (timeout == 100) { // wait max 10 seconds
                    TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
                    if (channel == null)
                        channel = context.getChannel();
                    channel.sendMessage(ERROR + format(context.getTranslated("music.nodetimeout"), Constants.DISCORD_URL, musicManager.getDebugString(context.getGuild(), player))).queue();
                    musicManager.dispose(context.getGuild());
                    locks.invalidate(context.getGuild());
                    return;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    locks.invalidate(context.getGuild());
                    Thread.currentThread().interrupt();
                }
                timeout++;
            }

            locks.invalidate(context.getGuild());
            player.playTrack(queue.poll().getAudioTrack()); // it shouldn't be null!
        } else
            player.setPaused(false);

        locks.invalidate(context.getGuild());
    }

    public static boolean lock(CommandContext context) {
        int u = 0;
        while (locks.getIfPresent(context.getGuild()) != null) {
            try {
                if (u == 100) {
                    context.send(ERROR + context.getTranslated("music.locked"));
                    return true;
                }
                Thread.sleep(100);
                u++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }
}
