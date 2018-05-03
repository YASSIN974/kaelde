package moe.kyokobot.social.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Member;

public class SendMoneyCommand extends Command {
    private final DatabaseManager databaseManager;

    public SendMoneyCommand(DatabaseManager databaseManager, EventWaiter eventWaiter) {
        this.databaseManager = databaseManager;
        name = "sendmoney";
        category = CommandCategory.SOCIAL;
        usage = "sendmoney.usage";
        description = "sendmoney.description";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length > 1) {
            try {
                int amount = Integer.parseInt(context.getArgs()[0]);
                String stringuser = context.skipConcatArgs(1);
                Member m = UserUtil.getMember(context.getGuild(), stringuser);
                if (m == null) {
                    CommonErrors.noUserFound(context, stringuser);
                } else {

                }
            } catch (NumberFormatException e) {
                CommonErrors.notANumber(context, context.getArgs()[0]);
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.exception(context, e);
            }
        } else {
            CommonErrors.usage(context);
        }
    }
}
