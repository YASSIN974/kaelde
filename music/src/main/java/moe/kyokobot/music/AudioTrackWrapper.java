package moe.kyokobot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.User;

public class AudioTrackWrapper {
    private AudioTrack audioTrack;
    private User user;
    private boolean marked;

    public AudioTrackWrapper(AudioTrack audioTrack, User user) {
        this.audioTrack = audioTrack;
        this.user = user;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public User getUser() {
        return user;
    }

    public boolean getMarked() {
        return marked;
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}
