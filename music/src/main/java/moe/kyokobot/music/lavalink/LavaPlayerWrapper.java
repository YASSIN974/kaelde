package moe.kyokobot.music.lavalink;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.Guild;
import samophis.lavalink.client.entities.LavaPlayer;
import samophis.lavalink.client.entities.State;

import javax.annotation.Nonnull;

public class LavaPlayerWrapper implements MusicPlayer {
    private final LavaPlayer player;

    public LavaPlayerWrapper(LavaPlayer player) {
        this.player = player;
    }

    public LavaPlayer getPlayer() {
        return player;
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

    @Override
    public boolean isConnected() {
        return player.getState() == State.CONNECTED;
    }
}
