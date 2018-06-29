package moe.kyokobot.music.util;

import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import lombok.Setter;

public class KaraokeFilter implements FloatPcmAudioFilter {
    private static final int BUFFER_SIZE = 4096;
    private static final float factor = .70710678f;

    public static final float DEFAULT_LEVEL = 1.0f;
    public static final float DEFAULT_MONO_LEVEL = 1.0f;
    public static final float DEFAULT_FILTER_BAND = 220.0f;
    public static final float DEFAULT_FILTER_WIDTH =100.0f;

    private FloatPcmAudioFilter downstream;
    @Setter
    private float level = DEFAULT_LEVEL;
    @Setter
    private float monoLevel = DEFAULT_MONO_LEVEL;
    @Setter
    private float filterBand = DEFAULT_FILTER_BAND;
    @Setter
    private float filterWidth = DEFAULT_FILTER_WIDTH;
    private float A, B, C, y1, y2 = 0f;

    public KaraokeFilter(FloatPcmAudioFilter downstream, int sampleRate) {
        this.downstream = downstream;

        C = (float) Math.exp(-2 * Math.PI * filterWidth / sampleRate);
        B = (float) (-4 * C / (1 + C) * Math.cos(2 * Math.PI * filterBand / sampleRate));
        A = (float) Math.sqrt(1 - B * B / (4 * C)) * (1 - C);
    }

    public KaraokeFilter(FloatPcmAudioFilter downstream, int sampleRate, float level, float filterBand, float filterWidth) {
        this.downstream = downstream;
        this.level = level;
        this.filterBand = filterBand;
        this.filterWidth = filterWidth;

        C = (float) Math.exp(-2 * Math.PI * filterWidth / sampleRate);
        B = (float) (-4 * C / (1 + C) * Math.cos(2 * Math.PI * filterBand / sampleRate));
        A = (float) Math.sqrt(1 - B * B / (4 * C)) * (1 - C);
    }

    @Override
    public void process(float[][] input, int offset, int length) throws InterruptedException {
        if (input.length != 2) return;
        float l, r, y, o = 0f;

        for (int i = 0; i < length; i++) {
            /* get left and right inputs */
            l = input[0][i];
            r = input[1][i];
            /* do filtering */
            y = (float) (A * ((l + r) / 2.0) - B * y1) - C * y2;
            y2 = y1;
            y1 = y;
            /* filter mono signal */
            o = y * monoLevel * level;
            /* now cut the center */
            input[0][i] = l - (r * level) + o;
            input[1][i] = r - (l * level) + o;
        }

        downstream.process(input, offset, length);
    }

    @Override
    public void seekPerformed(long requestedTime, long providedTime) {

    }

    @Override
    public void flush() throws InterruptedException {

    }

    @Override
    public void close() {

    }
}
