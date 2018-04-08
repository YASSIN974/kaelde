package me.gabixdev.kyoko.music;

import com.google.common.collect.ImmutableList;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.ResamplingPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import me.gabixdev.kyoko.Kyoko;
import net.dv8tion.jda.core.audio.AudioSendHandler;

import java.util.Arrays;
import java.util.Collections;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final Kyoko kyoko;
    private final AudioPlayer audioPlayer;
    private volatile AudioFrame lastFrame;

    private final int normalSpeed = 10;
    private final int maxSpeed = 10 * normalSpeed;
    private final int minSpeed = 1;
    private volatile int speed = 10;
    private int nightcore = 0;

    private volatile int count;

    public AudioPlayerSendHandler(Kyoko kyoko, AudioPlayer audioPlayer) {
        this.kyoko = kyoko;
        this.audioPlayer = audioPlayer;
        updateFilters();
    }

    @Override
    public boolean canProvide() {
        count += speed;

        if (lastFrame == null) {
            if (speed != 10) {
                if (audioPlayer.getPlayingTrack() != null && audioPlayer.getPlayingTrack().getDuration() == Long.MAX_VALUE) {
                    lastFrame = audioPlayer.provide();
                } else {
                    while (count >= normalSpeed) {
                        lastFrame = audioPlayer.provide();
                        count -= normalSpeed;
                    }
                }
            } else {
                lastFrame = audioPlayer.provide();
            }
        }

        return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.data : null;
        lastFrame = null;

        return data;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setNightcore(int nightcore) {
        this.nightcore = nightcore;
    }

    public int getNightcore() {
        return nightcore;
    }

    public void updateFilters() {
        if (audioPlayer.getPlayingTrack() != null && audioPlayer.getPlayingTrack().getDuration() == Long.MAX_VALUE) { // no support for streams
            audioPlayer.setFilterFactory(null);
        } else {
            if (nightcore != 0) {
                // TODO: enums? XD

                switch (nightcore) {
                    case 1:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 0.66f)
                        )));
                        return;
                    case 2:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 0.75f)
                        )));
                        return;
                    case 3:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 0.8f)
                        )));
                        return;
                    case 4:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 1.33f)
                        )));
                        return;
                    case 5:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 1.5f)
                        )));
                        return;
                    case 6:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 1.33f)
                        )));
                        return;
                    default:
                        audioPlayer.setFilterFactory(null);
                        audioPlayer.setFilterFactory((audioTrack, audioDataFormat, audioFilter) -> ImmutableList.of(new ResamplingPcmAudioFilter(
                                kyoko.getPlayerManager().getConfiguration(),
                                audioDataFormat.channelCount,
                                audioFilter,
                                audioDataFormat.sampleRate,
                                (int) (audioDataFormat.sampleRate * 0.66f)
                        )));
                        return;
                }
            } else {
                audioPlayer.setFilterFactory(null);
            }
        }
    }
}
