package moe.kyokobot.music.local;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class LocalSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public LocalSendHandler(LocalPlayerWrapper audioPlayer) {
        this.audioPlayer = audioPlayer.getPlayer();
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        return lastFrame.getData();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
