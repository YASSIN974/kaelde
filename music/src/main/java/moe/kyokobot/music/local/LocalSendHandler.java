package moe.kyokobot.music.local;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class LocalSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private final MutableAudioFrame frame;

    public LocalSendHandler(LocalPlayerWrapper audioPlayer) {
        this.audioPlayer = audioPlayer.getPlayer();
        this.frame = new MutableAudioFrame();
        AudioDataFormat format = StandardAudioDataFormats.DISCORD_OPUS;
        frame.setBuffer(ByteBuffer.allocate(format.maximumChunkSize()));
        frame.setFormat(format);
    }

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(frame);
    }

    @Override
    public byte[] provide20MsAudio() {
        return frame.getData();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
