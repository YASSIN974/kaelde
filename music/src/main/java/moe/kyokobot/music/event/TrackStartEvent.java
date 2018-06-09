package moe.kyokobot.music.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;

public class TrackStartEvent {
    private final MusicPlayer player;
    private final AudioTrack track;

    public TrackStartEvent(MusicPlayer player, AudioTrack track) {
        this.player = player;
        this.track = track;
    }

    public MusicPlayer getPlayer() {
        return player;
    }

    public AudioTrack getTrack() {
        return track;
    }
}
