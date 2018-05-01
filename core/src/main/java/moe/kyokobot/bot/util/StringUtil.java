package moe.kyokobot.bot.util;

import java.util.concurrent.TimeUnit;

public class StringUtil {
    public static String musicPrettyPeriod(long time) {
        if (time == Long.MAX_VALUE) return "streaming";
        return prettyPeriod(time);
    }

    public static String prettyPeriod(long time) {
        final long secs = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        final long mins = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        final long hours = TimeUnit.MILLISECONDS.toHours(time);
        return String.format("%02d:%02d:%02d", hours, mins, secs);
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
}
