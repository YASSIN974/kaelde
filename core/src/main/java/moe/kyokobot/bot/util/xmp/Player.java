package moe.kyokobot.bot.util.xmp;

import lombok.Getter;

import java.io.IOException;

@Getter
public class Player {
    private final int sampleRate;
    private final long instance;
    private Module module;

    public Player(int sampleRate) {
        XmpNativeLoader.loadKXMPLibrary();

        this.sampleRate = sampleRate;
        instance = Xmp.create();
    }

    public Module loadModule(byte[] data) throws IOException {
        final int code = Xmp.loadModule(instance, data);

        if (code < 0) {
            throw new IOException(Xmp.ERROR_STRING[-code]);
        }

        module = new Module(this);

        return module;
    }

    public int startPlayer() {
        return Xmp.startPlayer(instance, sampleRate);
    }

    public void endPlayer() {
        Xmp.endPlayer(instance);
    }

    public int getBuffer(byte[] data, int offset, int size) {
        return Xmp.playBuffer(instance, data, offset, size);
    }

    @Override
    @SuppressWarnings("ObjectFinalizeOverridenCheck")
    protected void finalize() throws Throwable {
        Xmp.destroy(instance);
        super.finalize();
    }
}
