package moe.kyokobot.bot.util.xmp;

public class Xmp {
    public static final int ERROR_INTERNAL = 2;
    public static final int ERROR_FORMAT = 3;
    public static final int ERROR_LOAD = 4;
    public static final int ERROR_SYSTEM = 6;
    public static final int ERROR_INVALID = 7;
    public static final int ERROR_STATE = 8;

    public static final String[] ERROR_STRING = {
            "No error",
            "End of module",
            "Internal error",
            "Unknown module format",
            "Can't load module",
            "Can't decompress module",
            "System error",
            "Invalid parameter",
            "Invalid player state"
    };

    private Xmp() {

    }

    native static long create();
    native static void destroy(long ctx);
    native static void getModData(long ctx, Module mod);
    native static int loadModule(long ctx, byte[] data);
    native static int playBuffer(long ctx, byte[] data, int offset, int length);
    native static int startPlayer(long ctx, int sampleRate);
    native static void endPlayer(long ctx);
}
