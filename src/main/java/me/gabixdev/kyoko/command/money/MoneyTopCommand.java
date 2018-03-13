package me.gabixdev.kyoko.command.money;

import com.j256.ormlite.stmt.QueryBuilder;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

import java.util.List;

public class MoneyTopCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"moneytop", "richest", "balancetop", "topbalance"};

    public MoneyTopCommand(Kyoko kyoko) {
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
    public CommandCategory getCategory() {
        return CommandCategory.MONEY;
    }

    @Override
    public String getDescription() {
        return "moneytop.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        QueryBuilder<UserConfig, Integer> qb = kyoko.getDatabaseManager().getUserDao().queryBuilder();
        qb.orderBy("money", false);
        qb.limit(10L);
        List<UserConfig> users = kyoko.getDatabaseManager().getUserDao().query(qb.prepare());

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < users.size(); i++) {
            UserConfig uc = users.get(i);
            User user = kyoko.getJda().getUserById(uc.userId);
            sb.append("`").append(i + 1).append(".` ");
            if (user != null) {
                sb.append(user.getName()).append("#").append(user.getDiscriminator());
            } else {
                sb.append("unknown:").append(uc.userId);
            }
            sb.append(" - ").append(uc.money).append("$\n");
        }

        eb.addField(kyoko.getI18n().get(l, "moneytop.title"), sb.toString(), false);
        message.getTextChannel().sendMessage(eb.build()).queue();
    }
}