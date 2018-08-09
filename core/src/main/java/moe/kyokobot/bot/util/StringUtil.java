package moe.kyokobot.bot.util;

import moe.kyokobot.bot.command.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

public class StringUtil {
    private StringUtil() {
        //
    }

    public static String markdown(String in) {
        return in.replace("`", "\\`");
    }

    public static List<String> splitString(String msg, int lineSize) {
        List<String> res = new ArrayList<String>();
        Pattern p = Pattern.compile("\\b.{1," + (lineSize-1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);

        while(m.find()) {
            res.add(m.group());
        }
        return res;
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

    public static Collector<String, List<String>, String> limitingJoin(String delimiter, int limit, String ellipsis) {
        return Collector.of(
            ArrayList::new,
            (l, e) -> {
                if (l.size() < limit) l.add(e);
                else if (l.size() == limit) l.add(ellipsis);
            },
            (l1, l2) -> {
                l1.addAll(l2.subList(0, Math.min(l2.size(), Math.max(0, limit - l1.size()))));
                if (l1.size() == limit) l1.add(ellipsis);
                return l1;
            },
            l -> String.join(delimiter, l)
        );
    }
}
