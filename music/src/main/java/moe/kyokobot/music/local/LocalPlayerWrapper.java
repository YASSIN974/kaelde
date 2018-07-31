package moe.kyokobot.music.local;

import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.github.natanbc.lavadsp.volume.VolumePcmAudioFilter;
import com.google.common.collect.ImmutableList;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.util.KaraokeFilter;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nonnull;

public class LocalPlayerWrapper implements MusicPlayer {
    static AudioConfiguration configuration;
    @Getter
    @Setter
    private AudioPlayer player;
    private final Guild guild;

    private float volume = 1.0f;
    @Getter
    private float nightcore = 1.0f;
    @Getter
    private boolean karaoke = false;
    @Getter
    private boolean vaporwave = false;
    private float karaokeWidth = KaraokeFilter.DEFAULT_FILTER_WIDTH;
    private float karaokeBand = KaraokeFilter.DEFAULT_FILTER_BAND;
    private float karaokeLevel = KaraokeFilter.DEFAULT_LEVEL;


    public LocalPlayerWrapper(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
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
        return (int) (volume * 100);
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
        this.volume = Math.abs((float) volume / 100);
        updateFilters(getPlayingTrack());
    }

    @Override
    public void setNightcore(float speed) {
        if (speed == 0.f) speed = 1.0f;
        speed = Math.abs(speed);
        nightcore = speed;
        updateFilters(getPlayingTrack());
    }

    @Override
    public void setKaraoke(boolean enabled) {
        this.karaoke = enabled;
        updateFilters(getPlayingTrack());
    }

    @Override
    public void setKaraokeWidth(float width) {
        this.karaokeWidth = width;
        updateFilters(getPlayingTrack());
    }

    @Override
    public void setKaraokeBand(float band) {
        this.karaokeBand = band;
        updateFilters(getPlayingTrack());
    }

    @Override
    public void setKaraokeLevel(float level) {
        this.karaokeLevel = level;
        updateFilters(getPlayingTrack());
    }

    @Override
    public void setVaporwave(boolean enabled) {
        this.vaporwave = enabled;
        updateFilters(getPlayingTrack());
    }

    @Override
    public boolean hasFiltersEnabled() {
        return nightcore != 1.0f || karaoke || vaporwave;
    }

    @Override
    public boolean isConnected() {
        return guild.getAudioManager().isConnected();
    }

    @Override
    public void updateFilters(AudioTrack track) {
        // _ _ _ _ _ V K N
        byte flags = 0;

        if (karaoke) flags |= 0b010;

        if (canChangeSpeed(track)) {
            if (nightcore != 1.0f) flags |= 0b001;
            if (vaporwave) flags |= 0b100;
        }

        if (flags == 0) {
            if (volume == 1.0f)
                player.setFilterFactory(null);
            else
                player.setFilterFactory((audioTrack, audioDataFormat, audioFilter) ->
                    ImmutableList.of(new VolumePcmAudioFilter(audioFilter, audioDataFormat.channelCount, audioDataFormat.sampleRate).setVolume(volume))
                );

        } else {
            final byte f = flags;

            player.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> {
                TimescalePcmAudioFilter timescale;
                if (volume == 1.0f)
                    timescale = new TimescalePcmAudioFilter(audioFilter, audioDataFormat.channelCount, audioDataFormat.sampleRate);
                else
                    timescale = new TimescalePcmAudioFilter(
                            new VolumePcmAudioFilter(audioFilter, audioDataFormat.channelCount, audioDataFormat.sampleRate).setVolume(volume),
                            audioDataFormat.channelCount,
                            audioDataFormat.sampleRate);

                if ((f & 0b001) != 0)
                    timescale.setRate(nightcore);
                if ((f & 0b100) != 0)
                    timescale.setSpeed(0.5f).setPitchSemiTones(-7);

                if (karaoke) {
                    if ((f & 0b101) != 0)
                        return ImmutableList.of(new KaraokePcmAudioFilter(timescale, audioDataFormat.channelCount, audioDataFormat.sampleRate));
                    else
                        return ImmutableList.of(new KaraokePcmAudioFilter(audioFilter, audioDataFormat.channelCount, audioDataFormat.sampleRate));
                } else {
                    return ImmutableList.of(timescale);
                }
            });
        }
    }

    private boolean canChangeSpeed(AudioTrack track) {
        if (track == null) return false;
        if (track.getSourceManager() instanceof YoutubeAudioSourceManager && track.getDuration() == Long.MAX_VALUE) return false;
        return !(track.getSourceManager() instanceof TwitchStreamAudioSourceManager);
    }
}
