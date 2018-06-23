package moe.kyokobot.music.local;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;

public class LocalPlayerWrapper implements MusicPlayer {
    private final AudioPlayer player;
    private final Guild guild;

    public LocalPlayerWrapper(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    @Override
    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    @Override
    public long getGuildId() {
        return guild.getIdLong();
    }

    @Override
    public long getChannelId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTimestamp() {
        return 0;
    }

    @Override
    public long getPosition() {
        return player.getPlayingTrack() != null ? player.getPlayingTrack().getPosition() : 0L;
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
        player.startTrack(track, false);
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
        player.destroy();
    }

    @Override
    public void seek(long position) {
        if (player.getPlayingTrack() != null)
            player.getPlayingTrack().setPosition(position);
    }

    @Override
    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    @Override
    public boolean isConnected() {
        return guild.getAudioManager().isConnected();
    }
}
