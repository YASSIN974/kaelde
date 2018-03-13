package me.gabixdev.kyoko.command.money;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class DailiesCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"dailies", "daily", "claim"};

    public DailiesCommand(Kyoko kyoko) {
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
        return "dailies.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        long time = System.currentTimeMillis();
        long claim = kyoko.getDatabaseManager().getUser(message.getAuthor()).claim;
        int amount = 175 + (int) Math.floor(Math.random() * 50);
        int money = kyoko.getDatabaseManager().getUser(message.getAuthor()).money;

        if (claim > time) {
            String claimtime = StringUtil.prettyPeriod((claim - time));
            EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            eb.addField(kyoko.getI18n().get(l, "money.title"), String.format(kyoko.getI18n().get(l, "money.wait"), claimtime), false);
            message.getTextChannel().sendMessage(eb.build()).queue();
        } else {
            money += amount;
            UserConfig uc = kyoko.getDatabaseManager().getUser(message.getAuthor());
            uc.claim = time + 86400000;
            uc.money = money;
            kyoko.getDatabaseManager().saveUser(message.getAuthor(), uc);

            EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            eb.addField(kyoko.getI18n().get(l, "money.title"), String.format(kyoko.getI18n().get(l, "money.claim"), amount, money), false);
            message.getTextChannel().sendMessage(eb.build()).queue();
        }
    }
}
