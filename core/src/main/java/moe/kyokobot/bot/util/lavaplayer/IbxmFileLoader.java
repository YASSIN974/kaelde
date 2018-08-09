package moe.kyokobot.bot.util.lavaplayer;

import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioProcessingContext;
import ibxm.Channel;
import ibxm.IBXM;
import ibxm.Module;

import java.io.IOException;

public class IbxmFileLoader {
    private final SeekableInputStream inputStream;

    public IbxmFileLoader(SeekableInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public IbxmTrackProvider loadTrack(AudioProcessingContext context) throws IOException {
        Module module = new Module( inputStream );
        IBXM ibxm = new IBXM( module, context.outputFormat.sampleRate );
        ibxm.setInterpolation(Channel.SINC);
        return new IbxmTrackProvider(context, ibxm);
    }
}
