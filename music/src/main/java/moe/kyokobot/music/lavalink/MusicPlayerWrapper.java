package moe.kyokobot.music.lavalink;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;
import samophis.lavalink.client.entities.LavaPlayer;

import javax.annotation.Nonnull;

public class MusicPlayerWrapper implements MusicPlayer {
    private final LavaPlayer player;

    public MusicPlayerWrapper(LavaPlayer player) {
        this.player = player;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    @Override
    public long getGuildId() {
        return player.getGuildId();
    }

    @Override
    public long getChannelId() {
        return player.getChannelId();
    }

    @Override
    public long getTimestamp() {
        return player.getTimestamp();
    }

    @Override
    public long getPosition() {
        return player.getPosition();
    }

    @Override
    public int getVolume() {
        return player.getVolume();
    }

    @Override
    public boolean isPaused() {
        return player.isPaused();
    }

    @Override
    public void playTrack(@Nonnull AudioTrack track) {
        player.playTrack(track);
    }

    @Override
    public void playTrack(@Nonnull AudioTrack track, long startTime, long endTime) {
        player.playTrack(track, startTime, endTime);
    }

    @Override
    public void playTrack(@Nonnull String identifier) {
        player.playTrack(identifier);
    }

    @Override
    public void playTrack(@Nonnull String identifier, long startTime, long endTime) {
        player.playTrack(identifier, startTime, endTime);
    }

    @Override
    public void stopTrack() {
        player.stopTrack();
    }

    @Override
    public void setPaused(boolean isPaused) {
        player.setPaused(isPaused);
    }

    @Override
    public void destroyPlayer() {
        player.destroyPlayer();
    }

    @Override
    public void seek(long position) {
        player.seek(position);
    }

    @Override
    public void setVolume(int volume) {
        player.setVolume(volume);
    }
}
