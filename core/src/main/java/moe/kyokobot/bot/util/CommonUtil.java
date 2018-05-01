package moe.kyokobot.bot.util;

import moe.kyokobot.bot.command.CommandContext;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;

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
}
