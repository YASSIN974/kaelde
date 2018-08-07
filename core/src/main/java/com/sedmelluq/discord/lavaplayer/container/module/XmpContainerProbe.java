package com.sedmelluq.discord.lavaplayer.container.module;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetectionResult;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerProbe;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import moe.kyokobot.bot.util.xmp.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection.UNKNOWN_ARTIST;

public class XmpContainerProbe implements MediaContainerProbe {
    private static final Logger log = LoggerFactory.getLogger(XmpContainerProbe.class);

    @Override
    public String getName() {
        return "xmp";
    }

    @Override
    public boolean matchesHints(MediaContainerHints hints) {
        return false;
    }

    @Override
    public MediaContainerDetectionResult probe(AudioReference reference, SeekableInputStream inputStream) throws IOException {
        byte[] buf = new byte[4 * 1024 * 1024];

        log.debug("Trying XMP...");
        Player p = new Player(44100);

        if (inputStream.read(buf) != 0) {
            try {
                p.loadModule(buf);
            } catch (IOException ee) {
                log.debug("xmp error: {}", ee.getMessage());
                buf = null;
                return null;
            }
        }

        buf = null;
        log.debug("Loaded module {} via XMP.", reference.identifier);

        inputStream.seek(0);

        return new MediaContainerDetectionResult(this, new AudioTrackInfo(
                p.getModule().getName(),
                UNKNOWN_ARTIST,
                Long.MAX_VALUE,
                reference.identifier,
                true,
                reference.identifier
        ));
    }

    @Override
    public AudioTrack createTrack(AudioTrackInfo trackInfo, SeekableInputStream inputStream) {
        return new XmpAudioTrack(trackInfo, inputStream);
    }
}
