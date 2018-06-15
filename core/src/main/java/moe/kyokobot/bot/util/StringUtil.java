package moe.kyokobot.bot.util;

import moe.kyokobot.bot.command.CommandContext;

import java.util.ArrayList;
import java.util.List;
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

    public static List<String> createPages(List<String> input) {
        ArrayList<String> out = new ArrayList<>();
        boolean renderpage = true;
        int pg = 0;
        while (renderpage) {
            int start = pg*10;
            int end = input.size() < (pg+1)*10 ? input.size() : (pg+1)*10;
            if (end <= start) {
                renderpage = false;
            } else {
                StringBuilder sbuilder = new StringBuilder();
                for (int i = start; i < end; i++)
                    sbuilder.append("`").append(i+1).append(".` ").append(input.get(i)).append("\n");
                out.add(sbuilder.toString());
                pg++;
            }
        }
        return out;
    }

    public static List<String> createRawPages(List<String> input) {
        ArrayList<String> out = new ArrayList<>();
        boolean renderpage = true;
        int pg = 0;
        while (renderpage) {
            int start = pg*10;
            int end = input.size() < (pg+1)*10 ? input.size() : (pg+1)*10;
            if (end <= start) {
                renderpage = false;
            } else {
                StringBuilder sbuilder = new StringBuilder();
                for (int i = start; i < end; i++)
                    sbuilder.append(i+1).append(". ").append(input.get(i)).append("\n");
                out.add(sbuilder.toString());
                pg++;
            }
        }
        return out;
    }

    public static String toggleFormat(CommandContext context, boolean toggle) {
        return context.getTranslated("generic." + (toggle ? "enabled" : "disabled"));
    }
}
