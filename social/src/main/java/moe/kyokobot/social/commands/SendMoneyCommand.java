package moe.kyokobot.social.commands;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.sentry.Sentry;
import moe.kyokobot.bot.command.*;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.TimeUnit;

public class SendMoneyCommand extends Command {
    private final DatabaseManager databaseManager;
    private Cache<User, SendMoneyRequest> requests;

    public SendMoneyCommand(DatabaseManager databaseManager) {
        name = "sendmoney";
        category = CommandCategory.SOCIAL;
        this.databaseManager = databaseManager;
        requests = Caffeine.newBuilder().expireAfterWrite(90, TimeUnit.SECONDS).maximumSize(500).build();
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length > 1) {
            sendMoney(context);
        } else CommonErrors.usage(context);
    }

    private void sendMoney(CommandContext context) {
        try {
            int amount = Integer.parseUnsignedInt(context.getArgs()[0]);
            String stringuser = context.skipConcatArgs(1);
            Member m = UserUtil.getMember(context.getGuild(), stringuser);
            if (m == null) {
                CommonErrors.noUserFound(context, stringuser);
            } else {
                if (context.getSender() == m.getUser()) {
                    context.send(CommandIcons.ERROR + context.getTranslated("sendmoney.self"));
                } else {
                    UserConfig sender = databaseManager.getUser(context.getSender());
                    if (sender.getMoney() >= amount) {
                        requests.put(context.getSender(), new SendMoneyRequest(amount, System.currentTimeMillis() + 30000, m.getUser()));
                        context.send(CommandIcons.INFO + String.format(context.getTranslated("sendmoney.request"), amount, UserUtil.toDiscrim(m.getUser()), context.getPrefix()));
                    } else {
                        context.send(CommandIcons.ERROR + context.getTranslated("sendmoney.nomoney"));
                    }
                }
            }
        } catch (NumberFormatException e) {
            CommonErrors.notANumber(context, context.getArgs()[0]);
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
            CommonErrors.exception(context, e);
        }
    }

    @SubCommand()
    public void confirm(CommandContext context) {
        SendMoneyRequest request = requests.getIfPresent(context.getSender());

        if (request != null) {
            requests.invalidate(context.getSender());
            if (request.isExpiried()) {
                context.send(CommandIcons.ERROR + context.getTranslated("sendmoney.expiried"));
            } else {
                try {
                    UserConfig sender = databaseManager.getUser(context.getSender());
                    UserConfig receiver = databaseManager.getUser(request.receiver);
                    if (sender.getMoney() >= request.amount) {
                        sender.setMoney(sender.getMoney() - request.amount);
                        receiver.setMoney(receiver.getMoney() + request.amount);
                        databaseManager.save(sender);
                        databaseManager.save(receiver);
                        context.send(CommandIcons.SUCCESS + String.format(context.getTranslated("sendmoney.sent"), request.amount, request.receiver.getName()));
                    } else {
                        context.send(CommandIcons.ERROR + context.getTranslated("sendmoney.nomoney"));
                    }
                } catch (Exception e) {
                    logger.error("Caught exception in SendMoneyCommand!", e);
                    Sentry.capture(e);
                    CommonErrors.exception(context, e);
                }
            }
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("sendmoney.expiried"));
        }
    }

    @SubCommand()
    public void cancel(CommandContext context) {
        if (requests.asMap().containsKey(context.getSender())) {
            requests.invalidate(context.getSender());
            context.send(CommandIcons.INFO + context.getTranslated("sendmoney.cancelled"));
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("sendmoney.expiried"));
        }
    }

    private class SendMoneyRequest {
        private final int amount;
        private final long expiry;
        private final User receiver;

        public SendMoneyRequest(int amount, long expiry, User receiver) {
            this.amount = amount;
            this.expiry = expiry;
            this.receiver = receiver;
        }

        public boolean isExpiried() {
            return expiry < System.currentTimeMillis();
        }
    }
}
