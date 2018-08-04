package moe.kyokobot.bot.util;

import moe.kyokobot.bot.command.CommandContext;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CommonUtil {
    public static boolean checkCooldown(HashMap<Guild, Long> cooldowns, CommandContext context, long time) {
        if (cooldowns != null) {
            Message message = context.getEvent().getMessage();
            if (cooldowns.containsKey(message.getGuild())) {
                if (cooldowns.get(message.getGuild()) > System.currentTimeMillis()) {
                    CommonErrors.cooldown(context);
                    return true;
                } else {
                    cooldowns.remove(message.getGuild());
                    cooldowns.put(message.getGuild(), System.currentTimeMillis() + time);
                }
            } else {
                cooldowns.put(message.getGuild(), System.currentTimeMillis() + time);
            }
        }
        return false;
    }

    public static String fromStream(InputStream stream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        bis.close();
        buf.close();
        return buf.toString("UTF-8");
    }

    public static <T> Set<T> reversedSet(Set<T> set) {
        List<T> list = new ArrayList<>(set);
        Collections.reverse(list);
        return new LinkedHashSet<>(list);
    }
}
