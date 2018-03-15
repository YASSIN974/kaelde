package me.gabixdev.kyoko.command.money;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.utils.tuple.MutableTriple;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SendMoneyCommand extends Command {

    private Kyoko kyoko;
    private final String[] aliases = new String[] {"sendmoney", "send"};
    public SendMoneyCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getUsage() {
        return "money.send.usage";
    }

    @Override
    public String getDescription() {
        return "money.send.description";
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        if(args.length < 2) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        } else if(!args[1].equalsIgnoreCase("confirm")){
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }
        if(args[1].equalsIgnoreCase("confirm")) {
            MutableTriple<User, Long, Integer> triple = kyoko.confirmMembers.get(message.getAuthor());
            if(triple != null) {
                if(triple.getMiddle() < System.currentTimeMillis()) {
                    EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                    builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "money.confirm.error"), false);
                    message.getTextChannel().sendMessage(builder.build()).queue();
                } else {
                    UserConfig mentionedConfig = kyoko.getDatabaseManager().getUser(triple.getLeft());
                    UserConfig senderConfig = kyoko.getDatabaseManager().getUser(message.getAuthor());
                    if(senderConfig.money < triple.getRight()) {
                        EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "money.send.amount"), false);
                        message.getTextChannel().sendMessage(builder.build()).queue();
                        return;
                    }
                    mentionedConfig.money += triple.getRight();
                    senderConfig.money -= triple.getRight();
                    kyoko.getDatabaseManager().saveUser(triple.getLeft(), mentionedConfig);
                    kyoko.getDatabaseManager().saveUser(message.getAuthor(), senderConfig);
                    EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                    builder.addField(kyoko.getI18n().get(l, "money.title"),
                            String.format(kyoko.getI18n().get(l, "money.sent"), triple.getRight(), triple.getLeft().getAsMention(), senderConfig.money, triple.getLeft().getAsMention(), mentionedConfig.money),
                            false);
                    message.getTextChannel().sendMessage(builder.build()).queue();
                    kyoko.confirmMembers.remove(message.getAuthor());
                }
            }
        } else {
            if(args.length < 3) {
                printUsage(kyoko, l, message.getTextChannel());
                return;
            }
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                CommonErrorUtil.notANumber(kyoko, l, message.getTextChannel(), args[1]);
                return;
            }
            String username = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));
            Member mentioned = UserUtil.getMember(message.getGuild(), username);
            if(mentioned == null) {
                CommonErrorUtil.noUserFound(kyoko, l, message.getTextChannel(), username);
                return;
            } else if (mentioned.getUser().isBot()) {
                CommonErrorUtil.isBot(kyoko, l, message.getTextChannel(), mentioned.getAsMention());
                return;
            }
            UserConfig senderConfig = kyoko.getDatabaseManager().getUser(message.getAuthor());
            if(senderConfig.money < amount) {
                EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "money.send.amount"), false);
                message.getTextChannel().sendMessage(builder.build()).queue();
                return;
            }
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            builder.addField(kyoko.getI18n().get(l, "money.title"), String.format(kyoko.getI18n().get(l, "money.confirm"), mentioned.getAsMention(), kyoko.getSettings().getPrefix() + args[0] + " confirm"), false);
            kyoko.confirmMembers.put(message.getAuthor(), MutableTriple.of(mentioned.getUser(), System.currentTimeMillis() + 60000, amount));
            message.getTextChannel().sendMessage(builder.build()).queue();

        }

    }
}
