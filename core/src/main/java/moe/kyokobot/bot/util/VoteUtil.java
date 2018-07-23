package moe.kyokobot.bot.util;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.manager.DatabaseManager;

public class VoteUtil {
    public static boolean voteLock(CommandContext context, DatabaseManager manager) {
        if (Globals.noVoteLock
                || !Globals.production
                || Globals.patreon
                || Constants.BOTLIST_GUILDS.contains(context.getGuild().getId())) return false;

        try {
            GuildConfig gconfig = manager.getGuild(context.getGuild());
            if (gconfig.isNoVoteLock()) return false;

            UserConfig config = manager.getUser(context.getSender());
            return config.getVoted() < (System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            return false;
        }
    }
}
