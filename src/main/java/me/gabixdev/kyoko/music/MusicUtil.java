package me.gabixdev.kyoko.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.exception.APIException;
import me.gabixdev.kyoko.util.exception.NotFoundException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class MusicUtil {
    public static void loadAndPlay(Kyoko kyoko, Language l, MusicManager mm, String url, final boolean addPlaylist) {
        loadAndPlay(kyoko, l, mm, url, addPlaylist, false);
    }

    public static void loadAndPlay(Kyoko kyoko, Language l, MusicManager mm, String url, final boolean addPlaylist, boolean triedYt) {
        TextChannel channel = mm.outChannel;
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link resources
        if (url.startsWith("<") && url.endsWith(">"))
            trackUrl = url.substring(1, url.length() - 1);
        else
            trackUrl = url;

        kyoko.getPlayerManager().loadItemOrdered(mm, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.queued"), track.getInfo().title), false);
                channel.sendMessage(err.build()).queue();
                mm.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                List<AudioTrack> tracks = playlist.getTracks();

                if (addPlaylist) {
                    EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                    err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.playlistadded"), playlist.getTracks().size(), playlist.getName()), false);
                    channel.sendMessage(err.build()).queue();

                    tracks.forEach(mm.scheduler::queue);
                } else {
                    EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                    err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.queued"), firstTrack.getInfo().title), false);
                    channel.sendMessage(err.build()).queue();

                    tracks.forEach(mm.scheduler::queue);
                }
            }

            @Override
            public void noMatches() {
                if (triedYt) {
                    EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                    err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.cantload"), url, kyoko.getSettings().getPrefix()), false);
                    channel.sendMessage(err.build()).queue();
                } else { // try to search and play
                    String link = getFirstLink(channel, l, kyoko, url);
                    if (link == null) {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.cantload"), url, kyoko.getSettings().getPrefix()), false);
                        channel.sendMessage(err.build()).queue();
                        return;
                    } else {
                        loadAndPlay(kyoko, l, mm, link, addPlaylist, true);
                    }
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.error"), exception.getMessage()), false);
                channel.sendMessage(err.build()).queue();
            }
        });
    }

    private static String getFirstLink(TextChannel channel, Language l, Kyoko kyoko, String query) {
        if (query.startsWith("http://") || query.startsWith("https://")) return query;

        try {
            SearchResult sr = YoutubeSearch.search(query);
            if (sr.getEntries().isEmpty()) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.notfound"), query), false);
                channel.sendMessage(err.build()).queue();
                return null;
            } else {
                return sr.getEntries().get(0).getURL();
            }
        } catch (NotFoundException ex) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.notfound"), query), false);
            channel.sendMessage(err.build()).queue();
            return null;
        } catch (Exception ex) {
            kyoko.getLog().severe(ex.getMessage());
            ex.printStackTrace();

            if (ex instanceof APIException) {
                kyoko.getLog().severe("Data: " + ((APIException) ex).getRaw());
            }
            channel.sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "generic.error.message"), Constants.DISCORD_URL), false).build()).queue();
            channel.sendMessage(Constants.DISCORD_URL).queue();
            return null;
        }
    }
}
