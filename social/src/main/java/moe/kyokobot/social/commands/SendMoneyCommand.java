package moe.kyokobot.social.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

public class SendMoneyCommand extends Command {
    private final DatabaseManager databaseManager;
    private HashMap<User, SendMoneyRequest> requests;

    public SendMoneyCommand(DatabaseManager databaseManager, EventWaiter eventWaiter) {
        name = "sendmoney";
        category = CommandCategory.SOCIAL;
        usage = "sendmoney.usage";
        description = "sendmoney.description";

        this.databaseManager = databaseManager;

        requests = new HashMap<>();
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length > 1) {
            sendMoney(context);
        } else if (context.getArgs().length == 1) {
            switch (context.getArgs()[0].toLowerCase()) {
                case "confirm":
                    confirm(context);
                    break;
                case "cancel":
                    cancel(context);
                    break;
                default:
                    CommonErrors.usage(context);
                    break;
            }
        } else CommonErrors.usage(context);
    }

    private void sendMoney(CommandContext context) {
        try {
            int amount = Integer.parseInt(context.getArgs()[0]);
            String stringuser = context.skipConcatArgs(1);
            Member m = UserUtil.getMember(context.getGuild(), stringuser);
            if (m == null) {
                CommonErrors.noUserFound(context, stringuser);
            } else {
                if (context.getSender() == m.getUser()) {
                    context.send(context.error() + context.getTranslated("sendmoney.self"));
                } else {
                    UserConfig sender = databaseManager.getUser(context.getSender());
                    if (sender.money >= amount) {
                        requests.put(context.getSender(), new SendMoneyRequest(amount, System.currentTimeMillis() + 30000, m.getUser()));
                        context.send(context.info() + String.format(context.getTranslated("sendmoney.request"), amount, m.getEffectiveName(), context.getPrefix()));
                    } else {
                        context.send(context.error() + context.getTranslated("sendmoney.nomoney"));
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

    private void confirm(CommandContext context) {
        if (requests.containsKey(context.getSender())) {
            SendMoneyRequest request = requests.remove(context.getSender());
            if (request.isExpiried()) {
                context.send(context.error() + context.getTranslated("sendmoney.expiried"));
            } else {
                try {
                    UserConfig sender = databaseManager.getUser(context.getSender());
                    UserConfig receiver = databaseManager.getUser(request.receiver);
                    if (sender.money >= request.amount) {
                        sender.money -= request.amount;
                        receiver.money += request.amount;
                        databaseManager.saveUser(context.getSender(), sender);
                        databaseManager.saveUser(request.receiver, receiver);
                        context.send(context.success() + String.format(context.getTranslated("sendmoney.sent"), request.amount, request.receiver.getName()));
                    } else {
                        context.send(context.error() + context.getTranslated("sendmoney.nomoney"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Sentry.capture(e);
                    CommonErrors.exception(context, e);
                }
            }
        } else {
            context.send(context.error() + context.getTranslated("sendmoney.expiried"));
        }
    }

    private void cancel(CommandContext context) {
        if (requests.containsKey(context.getSender())) {
            requests.remove(context.getSender());
            context.send(context.info() + context.getTranslated("sendmoney.cancelled"));
        } else {
            context.send(context.error() + context.getTranslated("sendmoney.expiried"));
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
