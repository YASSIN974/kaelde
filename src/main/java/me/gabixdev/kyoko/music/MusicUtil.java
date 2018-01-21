package me.gabixdev.kyoko.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class MusicUtil {
    public static void loadAndPlay(Kyoko kyoko, Language l, MusicManager mm, String url, final boolean addPlaylist) {
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
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.notfound"), url), false);
                channel.sendMessage(err.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.error"), exception.getMessage()), false);
                channel.sendMessage(err.build()).queue();
            }
        });
    }
}
