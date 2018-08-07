package moe.kyokobot.bot.util.xmp;

import com.sedmelluq.discord.lavaplayer.natives.NativeLibLoader;

public class XmpNativeLoader {
    private static volatile boolean loaded = false;
    private static volatile boolean criticalNativesAvailable;

    public static void loadKXMPLibrary() {
        if(loaded) return;
        NativeLibLoader.load(XmpNativeLoader.class, "kxmp");
        //criticalNativesAvailable = Xmp.criticalMethodsAvailable();
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    /*public static boolean areCriticalNativesAvailable() {
        loadKXMPLibrary();
        //return criticalNativesAvailable;
    }*/
}

