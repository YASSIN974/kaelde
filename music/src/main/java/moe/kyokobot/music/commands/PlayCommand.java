package moe.kyokobot.music.commands;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.music.MusicIcons.PLAY;
import static moe.kyokobot.music.MusicUtil.locks;

public class PlayCommand extends MusicCommand {
    private final MusicManager musicManager;
    private final SearchManager searchManager;

    public PlayCommand(MusicManager musicManager, SearchManager searchManager) {
        this.musicManager = musicManager;
        this.searchManager = searchManager;

        name = "play";
        description = "music.play.description";
        aliases = new String[] {">", "p"};
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (MusicUtil.lock(context)) return;

        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            locks.put(context.getGuild(), true);
            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            if (context.hasArgs()) {
                queue.setAnnouncing(context.getChannel(), context);

                if (loadTracks(context, queue))
                    MusicUtil.play(musicManager, player, queue, context, voiceChannel);

                locks.invalidate(context.getGuild());
            } else {
                if (!context.getMessage().getAttachments().isEmpty()) {
                    if (loadTracksFromAttachment(context, queue))
                        MusicUtil.play(musicManager, player, queue, context, voiceChannel);
                } else if (player.isPaused()) {
                    queue.setAnnouncing(context.getChannel(), context);
                    player.setPaused(false);
                    context.send(MusicIcons.PLAY + context.getTranslated("music.resumed"));
                    locks.invalidate(context.getGuild());
                } else {
                    CommonErrors.usage(context);
                }
            }
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.joinchannel"));
        }
    }

    @SubCommand
    public void debug(CommandContext context) {
        context.send("```ldif\n" + musicManager.getDebug() + "\n" + searchManager.getDebug() + "\n```");
    }

    @SubCommand
    public void formats(CommandContext context) {
        context.send("YouTube, SoundCloud, Twitch, Bandcamp, NicoNico, ");
    }


    private boolean loadTracksFromAttachment(CommandContext context, MusicQueue queue) {
        AudioTrack track;
        int items = 0;

        for (Message.Attachment attachment : context.getMessage().getAttachments()) {
            try {
                track = (AudioTrack) musicManager.resolve(context.getGuild(), attachment.getUrl());
                queue.add(track);
                items++;
            } catch (Exception e) {
                context.send(CommandIcons.ERROR + String.format(context.getTranslated("music.error"), e.getMessage()));
                return false;
            }
        }

        context.send(MusicIcons.PLAY + String.format(context.getTranslated("music.addeditems"), items));
        return true;
    }

    private boolean loadTracks(CommandContext context, MusicQueue queue) {
        String query = context.getConcatArgs().trim();
        if (query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + "play")
                || query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + "p")
                || query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + ">")) {
            context.send(CommandIcons.ERROR + String.format(context.getTranslated("music.nothingfound"), query));
            return false; // the user is retarded, do not waste resources to query the track
        }

        try {
            AudioItem item = musicManager.resolve(context.getGuild(), query);

            if (item == null) {
                SearchManager.SearchResult result = searchManager.searchYouTube(query);
                if (result != null && result.getEntries() != null && !result.getEntries().isEmpty()) {
                    item = musicManager.resolve(context.getGuild(), result.getEntries().get(0).getUrl());
                } else {
                    context.send(CommandIcons.ERROR + String.format(context.getTranslated("music.nothingfound"), query));
                    locks.invalidate(context.getGuild());
                    return false;
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
            } else if (item instanceof AudioReference) {
                if (((AudioReference) item).identifier == null) {
                    context.send(CommandIcons.ERROR + context.getTranslated("music.agerestricted"));
                    return false;
                }
            } else {
                logger.debug("Unknown item type: " + item.getClass().getName());
                return false;
            }
        } catch (FriendlyException e) {
            if (e.getMessage().equals("The playlist is private.")) {
                context.send(CommandIcons.ERROR + context.getTranslated("music.privateplaylist"));
            } else {
                context.send(CommandIcons.ERROR + String.format(context.getTranslated("music.error"), e.getMessage()));
            }
            return false;
        }
        return true;
    }
}
