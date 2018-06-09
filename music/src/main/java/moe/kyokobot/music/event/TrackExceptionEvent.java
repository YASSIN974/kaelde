package moe.kyokobot.music.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;

public class TrackExceptionEvent {
    private final MusicPlayer player;
    private final AudioTrack track;
    private final Exception exception;

    public TrackExceptionEvent(MusicPlayer player, AudioTrack track, Exception exception) {
        this.player = player;
        this.track = track;
        this.exception = exception;
    }

    public MusicPlayer getPlayer() {
        return player;
    }
    public AudioTrack getTrack() {
        return track;
    }
    public Exception getException() {
        return exception;
    }
}
