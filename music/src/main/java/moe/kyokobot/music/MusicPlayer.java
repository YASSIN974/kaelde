package moe.kyokobot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import javax.annotation.Nonnull;

public interface MusicPlayer {
    AudioTrack getPlayingTrack();
    long getGuildId();
    long getChannelId();
    long getTimestamp();
    long getPosition();
    int getVolume();
    boolean isPaused();
    void playTrack(@Nonnull AudioTrack track);
    void playTrack(@Nonnull AudioTrack track, long startTime, long endTime);
    void playTrack(@Nonnull String identifier);
    void playTrack(@Nonnull String identifier, long startTime, long endTime);
    void stopTrack();
    void setPaused(boolean isPaused);
    void destroyPlayer();
    void seek(long position);
    void setVolume(int volume);
    boolean isConnected();
}
