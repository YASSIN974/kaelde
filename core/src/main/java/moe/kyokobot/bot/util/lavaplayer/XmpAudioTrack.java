package moe.kyokobot.bot.util.lavaplayer;

import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BaseAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmpAudioTrack extends BaseAudioTrack {
    private static final Logger log = LoggerFactory.getLogger(XmpAudioTrack.class);

    private final SeekableInputStream inputStream;

    /**
     * @param trackInfo Track info
     * @param inputStream Input stream for the WAV file
     */
    public XmpAudioTrack(AudioTrackInfo trackInfo, SeekableInputStream inputStream) {
        super(trackInfo);

        this.inputStream = inputStream;
    }

    @Override
    public void process(LocalAudioTrackExecutor localExecutor) throws Exception {
        XmpTrackProvider trackProvider = new XmpFileLoader(inputStream).loadTrack(localExecutor.getProcessingContext());

        try {
            log.debug("Starting to play module {}", getIdentifier());
            localExecutor.executeProcessingLoop(trackProvider::provideFrames, trackProvider::seekToTimecode);
        } finally {
            trackProvider.close();
        }
    }
}
