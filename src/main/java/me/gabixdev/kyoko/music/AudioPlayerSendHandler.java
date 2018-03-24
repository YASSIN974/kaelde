package me.gabixdev.kyoko.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    private final int normalSpeed = 10;
    private final int maxSpeed = 10 * normalSpeed;
    private final int minSpeed = 1;
    private int speed = 10;

    private int count;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        count += speed;

        if (lastFrame == null) {
            if (speed != 10) {
                while (count >= normalSpeed) {
                    lastFrame = audioPlayer.provide();
                    count -= normalSpeed;
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
}
