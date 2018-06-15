package moe.kyokobot.music.commands;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.*;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;

import java.util.concurrent.TimeUnit;

import static moe.kyokobot.music.MusicIcons.PLAY;

public class PlayCommand extends MusicCommand {
    private final MusicManager musicManager;
    private final SearchManager searchManager;
    private Cache<Guild, Boolean> locks = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(2000).build();

    public PlayCommand(MusicManager musicManager, SearchManager searchManager) {
        this.musicManager = musicManager;
        this.searchManager = searchManager;

        name = "play";
        description = "music.play.description";
        aliases = new String[] {">", "p"};
    }

    @Override
    public void execute(CommandContext context) {
        if (locks.getIfPresent(context.getGuild()) != null) {
            context.send(CommandIcons.error + context.getTranslated("music.locked"));
            return;
        }
        if (context.hasArgs()) {
            VoiceChannel voiceChannel = MusicUtil.getCurrentChannel(context.getGuild(), context.getMember());
            if (voiceChannel != null) {
                locks.put(context.getGuild(), true);
                MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
                MusicQueue queue = musicManager.getQueue(context.getGuild());
                AudioItem item = musicManager.resolve(context.getConcatArgs().trim());

                // TODO: enable/disable announcing
                queue.setAnnouncing(context.getChannel(), context);

                if (item == null) {
                    SearchManager.SearchResult result = searchManager.searchYouTube(context.getConcatArgs());
                    if (result == null) {
                        context.send(CommandIcons.error + String.format(context.getTranslated("music.nothingfound"), context.getConcatArgs()));
                        locks.invalidate(context.getGuild());
                        return;
                    } else {
                        item = musicManager.resolve(result.getEntries().get(0).getUrl());
                    }
                }

                if (item instanceof AudioPlaylist) {
                    int tracks = 0;
                    for (AudioTrack track : ((AudioPlaylist) item).getTracks()) {
                        queue.add(track);
                        tracks++;
                    }
                    context.send(PLAY + String.format(context.getTranslated("music.addedplaylist"), tracks, ((AudioPlaylist) item).getName().replace("`", "\\`")));
                } else if (item instanceof AudioTrack) {
                    queue.add((AudioTrack) item);
                    context.send(PLAY + String.format(context.getTranslated("music.added"), ((AudioTrack) item).getInfo().title.replace("`", "\\`")));
                }

                if (player.getPlayingTrack() == null) {
                    int timeout = 0;
                    musicManager.openConnection((JDAImpl) context.getEvent().getJDA(), context.getGuild(), voiceChannel);

                    while (!player.isConnected()) { // wait for connect
                        if (timeout == 100) { // wait max 10 seconds
                            context.send(CommandIcons.error + "Music node connect timeout! Try using `{prefix}stop` to reset audio connection".replace("{prefix}", context.getPrefix()));
                            locks.invalidate(context.getGuild());
                            return;
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            locks.invalidate(context.getGuild());
                            // ignored
                        }
                        timeout++;
                    }
                    player.playTrack(queue.poll());
                    locks.invalidate(context.getGuild());
                }
            } else {
                context.send(CommandIcons.error + context.getTranslated("music.joinchannel"));
            }
        } else {
            CommonErrors.usage(context);
        }
    }

    @SubCommand
    public void debug(CommandContext context) {
        context.send("```\n" + musicManager.getDebug() + "\n" + searchManager.getDebug() + "\n```");
    }
}
