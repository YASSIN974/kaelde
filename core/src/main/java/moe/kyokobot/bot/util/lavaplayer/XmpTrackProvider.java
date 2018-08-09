package moe.kyokobot.bot.util.lavaplayer;

import com.sedmelluq.discord.lavaplayer.filter.AudioPipeline;
import com.sedmelluq.discord.lavaplayer.filter.AudioPipelineFactory;
import com.sedmelluq.discord.lavaplayer.filter.PcmFormat;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioProcessingContext;
import moe.kyokobot.bot.util.xmp.Player;
import moe.kyokobot.bot.util.xmp.Xmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class XmpTrackProvider {
    private int BLOCKS_IN_BUFFER = 2048;
    private final Player player;
    private final AudioPipeline downstream;

    public XmpTrackProvider(AudioProcessingContext context, Player player) {
        this.player = player;
        this.downstream = AudioPipelineFactory.create(context, new PcmFormat(2, player.getSampleRate()));
    }

    public void seekToTimecode(long timecode) {
        // TODO
    }

    public void provideFrames() throws InterruptedException {
        try {
            byte[] buffer = new byte[BLOCKS_IN_BUFFER * 2];
            short[] shortBuffer = new short[BLOCKS_IN_BUFFER];

            int e = player.startPlayer();
            if (e != 0)
                throw new IOException(Xmp.ERROR_STRING[-e]);

            while (e == 0) {
                e = player.getBuffer(buffer, 0, buffer.length);
                if (e != 0) {
                    System.out.println("Error: " + Xmp.ERROR_STRING[-e]);
                }

                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortBuffer);
                downstream.process(shortBuffer, 0, BLOCKS_IN_BUFFER);
            }
            player.endPlayer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        downstream.close();
    }
}
