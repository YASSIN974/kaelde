package moe.kyokobot.music.local;

import com.google.common.collect.ImmutableList;
import com.sedmelluq.discord.lavaplayer.filter.ResamplingPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;

public class LocalPlayerWrapper implements MusicPlayer {
    static AudioConfiguration configuration;
    private final AudioPlayer player;
    private final Guild guild;

    private float nightcore = 1.0f;

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
    public float getNightcore() {
        return nightcore;
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
    public void setNightcore(float speed) {
        if (speed == 0.f) speed = 1.0f;
        speed = Math.abs(speed);
        nightcore = speed;

        if (nightcore == 1.0f) {
            player.setFilterFactory(null);
        } else {
            player.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                    configuration,
                    audioDataFormat.channelCount,
                    audioFilter,
                    audioDataFormat.sampleRate,
                    (int) (audioDataFormat.sampleRate * (1f / nightcore))
            )));
        }
    }

    @Override
    public boolean isConnected() {
        return guild.getAudioManager().isConnected();
    }
}
