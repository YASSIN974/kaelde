package moe.kyokobot.bot.util.lavaplayer;

import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioProcessingContext;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.util.xmp.Player;

import java.io.IOException;

public class XmpFileLoader {
    private final SeekableInputStream inputStream;

    public XmpFileLoader(SeekableInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public XmpTrackProvider loadTrack(AudioProcessingContext context) throws IOException {
        byte[] data;
        if (!Globals.production && Globals.patreon) {
            data = new byte[16 * 1024 * 1024]; // 16 MB of buffer for patreons and selfhosters
        } else {
            data = new byte[4 * 1024 * 1024];
        }
        int i = 0;
        int d = 0;

        while (d != -1) {
            d = inputStream.read();
            if (i >= data.length)
                throw new IOException("Module too big to fit in buffer!"); // limit module size to 4 MB

            data[i] = (byte) d;
            i++;
        }

        Player player = new Player(context.outputFormat.sampleRate);
        player.loadModule(data);

        return new XmpTrackProvider(context, player);
    }
}
