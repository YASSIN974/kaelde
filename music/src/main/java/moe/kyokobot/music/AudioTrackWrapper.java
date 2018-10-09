package moe.kyokobot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioTrackWrapper {
    private AudioTrack audioTrack;
    private String user;

    public AudioTrackWrapper(AudioTrack audioTrack, String user) {
        this.audioTrack = audioTrack;
        this.user = user;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public String getUser() {
        return user;
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
