package me.gabixdev.kyoko.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final MusicManager m;
    private final Kyoko kyoko;

    public TrackScheduler(AudioPlayer player, Kyoko kyoko, MusicManager m) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.m = m;
        this.kyoko = kyoko;
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        AudioTrack track = queue.poll();

        if (m.outChannel != null && track != null) {
            Language l = kyoko.getI18n().getLanguage(m.guild);
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.playing"), track.getInfo().title, StringUtil.prettyPeriod(track.getDuration())), false);
            m.outChannel.sendMessage(err.build()).queue();
        }
        player.startTrack(track, false);
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    public ArrayList<AudioTrack> getTracks() {
        return new ArrayList<>(queue);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}

