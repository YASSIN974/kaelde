package moe.kyokobot.social.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Member;

import static moe.kyokobot.social.SocialConstants.MONEY_PREFIX;

public class ClaimCommand extends Command {
    private final DatabaseManager databaseManager;

    public ClaimCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.name = "claim";
        this.category = CommandCategory.SOCIAL;
        this.aliases = new String[] {"dailies"};
        this.description = "claim.description";
        this.usage = "generic.useronlyusage";
    }

    @Override
    public void execute(CommandContext context) {
        try {
            long currentTime = System.currentTimeMillis();
            UserConfig uc = databaseManager.getUser(context.getSender());

            if (uc.claim > currentTime) {
                context.send(context.error() + String.format(context.getTranslated("claim.wait"), StringUtil.prettyPeriod(uc.claim - currentTime)));
            } else {
                if (context.hasArgs()) {
                    Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
                    if (member == null) {
                        context.send(context.error() + String.format(context.getTranslated("generic.usernotfound"), context.getConcatArgs()));
                    } else {
                        UserConfig desireduc = databaseManager.getUser(member.getUser());
                        int money = 200 + (int) Math.floor(Math.random() * 50);
                        desireduc.money += money;
                        uc.claim = currentTime + 86400000;
                        databaseManager.save(uc);
                        databaseManager.save(desireduc);
                        context.send(MONEY_PREFIX + String.format(context.getTranslated("claim.given"), context.getSender().getAsMention(), money, member.getUser().getAsMention()));
                    }
                } else {
                    int money = 150 + (int) Math.floor(Math.random() * 50);
                    uc.money += money;
                    uc.claim = currentTime + 86400000;
                    databaseManager.save(uc);
                    context.send(MONEY_PREFIX + String.format(context.getTranslated("claim.claimed"), context.getSender().getAsMention(), money));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
            CommonErrors.exception(context, e);
        }
    }
}
