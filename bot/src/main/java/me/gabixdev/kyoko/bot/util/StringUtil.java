package me.gabixdev.kyoko.bot.util;

import net.dv8tion.jda.core.entities.User;

public class StringUtil {
    public static String formatDiscrim(User u) {
        return u.getName() + "#" + u.getDiscriminator();
    }
}
