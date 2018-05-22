package moe.kyokobot.music.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;

public class TrackStuckEvent {
    private final MusicPlayer player;
    private final AudioTrack track;
    private final long threshold;
    public TrackStuckEvent(MusicPlayer player, AudioTrack track, long threshold) {
        this.player = player;
        this.track = track;
        this.threshold = threshold;
    }
    public MusicPlayer getPlayer() {
        return player;
    }
    public AudioTrack getTrack() {
        return track;
    }
    public long getThreshold() {
        return threshold;
    }
}
