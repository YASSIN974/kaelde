package moe.kyokobot.music.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import moe.kyokobot.music.MusicPlayer;

public class TrackEndEvent {
    private final MusicPlayer player;
    private final AudioTrack track;
    private final AudioTrackEndReason reason;

    public TrackEndEvent(MusicPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        this.player = player;
        this.track = track;
        this.reason = reason;
    }

    public MusicPlayer getPlayer() {
        return player;
    }
    public AudioTrack getTrack() {
        return track;
    }
    public AudioTrackEndReason getReason() {
        return reason;
    }
}
