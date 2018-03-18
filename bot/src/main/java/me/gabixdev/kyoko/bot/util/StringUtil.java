package me.gabixdev.kyoko.bot.util;

import net.dv8tion.jda.core.entities.User;

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
}
