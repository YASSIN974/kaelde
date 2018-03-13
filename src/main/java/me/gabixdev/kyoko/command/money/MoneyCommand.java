package me.gabixdev.kyoko.command.money;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class MoneyCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"money", "balance", "bal"};

    public MoneyCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "money.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        if (args.length == 1) {
            int money = kyoko.getDatabaseManager().getUser(message.getMember().getUser()).money;
            eb.addField(kyoko.getI18n().get(l, "money.title"), String.format(kyoko.getI18n().get(l, "money.your"), money), false);
            message.getTextChannel().sendMessage(eb.build()).queue();
        } else {
            String name = message.getContentRaw().substring(args[0].length() + kyoko.getSettings().getPrefix().length() + 1);
            Member member = UserUtil.getMember(message.getGuild(), name);
            if (member == null) {
                CommonErrorUtil.noUserFound(kyoko, l, message.getTextChannel(), name);
            } else {
                int money = kyoko.getDatabaseManager().getUser(member.getUser()).money;
                eb.addField(kyoko.getI18n().get(l, "money.title"), String.format(kyoko.getI18n().get(l, "money.other"), member.getEffectiveName(), money), false);
                message.getTextChannel().sendMessage(eb.build()).queue();
            }
        }
    }
}