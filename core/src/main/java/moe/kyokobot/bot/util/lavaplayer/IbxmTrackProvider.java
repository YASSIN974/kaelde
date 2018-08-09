package moe.kyokobot.bot.util.lavaplayer;

import com.sedmelluq.discord.lavaplayer.filter.AudioPipeline;
import com.sedmelluq.discord.lavaplayer.filter.AudioPipelineFactory;
import com.sedmelluq.discord.lavaplayer.filter.PcmFormat;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioProcessingContext;
import ibxm.IBXM;

public class IbxmTrackProvider {
    private int BLOCKS_IN_BUFFER = 2048;
    private final IBXM ibxm;
    private final AudioPipeline downstream;

    public IbxmTrackProvider(AudioProcessingContext context, IBXM ibxm) {
        this.ibxm = ibxm;
        this.downstream = AudioPipelineFactory.create(context, new PcmFormat(2, ibxm.getSampleRate()));
        BLOCKS_IN_BUFFER = ibxm.getMixBufferLength();
    }

    public void seekToTimecode(long timecode) {
        // TODO
    }

    public void provideFrames() throws InterruptedException {
        try {
            int blockCount;
            int[] buffer = new int[BLOCKS_IN_BUFFER];
            short[] shortBuffer = new short[BLOCKS_IN_BUFFER];

            while ((blockCount = ibxm.getAudio(buffer)) > 0) {
                for(int i = 0; i < BLOCKS_IN_BUFFER; i++)
                {
                    shortBuffer[i] = (short)buffer[i];
                }
                downstream.process(shortBuffer, 0, blockCount * 2);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        downstream.close();
    }
}
