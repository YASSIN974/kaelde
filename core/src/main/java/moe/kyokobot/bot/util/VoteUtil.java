package moe.kyokobot.bot.util;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;

public class VoteUtil {
    public static boolean voteLock(CommandContext context, DatabaseManager manager) {
        if (Constants.BOTLIST_GUILDS.contains(context.getGuild().getId())) return false;
        
        return false;
    }
}
