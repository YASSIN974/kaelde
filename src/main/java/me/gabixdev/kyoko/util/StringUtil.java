package me.gabixdev.kyoko.util;

import java.util.concurrent.TimeUnit;

public class StringUtil {
    public static int getOccurencies(String string, String subString) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = string.indexOf(subString, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += subString.length();
            }
        }

        return count;
    }

    public static String prettyPeriod(long millis) {
        // because java builtin methods sucks...

        final long secs = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        final long mins = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);

        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}