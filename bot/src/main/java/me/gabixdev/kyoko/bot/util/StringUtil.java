package me.gabixdev.kyoko.bot.util;

import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class StringUtil {
    public static String formatDiscrim(User u) {
        return u.getName() + "#" + u.getDiscriminator();
    }

    public static String zeroHexFill(String s) {
        if (s.length() < 4) {

            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < 4)
                sb.insert(0, "0");

            return sb.toString();
        }
        return s;
    }

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
        if (millis == Long.MAX_VALUE) return "streaming";
        // because java builtin methods sucks...

        final long secs = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        final long mins = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);

        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}
