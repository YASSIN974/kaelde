package me.gabixdev.kyoko.command.util;

import com.google.gson.JsonArray;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class CryptoTopCommand extends Command {
    private final String[] aliases = new String[]{"cryptotop"};
    private Kyoko kyoko;

    public CryptoTopCommand(Kyoko kyoko) {
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
        return "cryptotop.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.UTILITY;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        StringBuilder s = new StringBuilder("");

        JsonArray data = GsonUtil.fromStringToJsonElement(URLUtil.readUrl("https://api.coinmarketcap.com/v1/ticker/?limit=10")).getAsJsonArray();
        data.forEach((jsonElement) -> {
            s.append("**").append(jsonElement.getAsJsonObject().get("rank").getAsString()).append(". ");
            s.append(jsonElement.getAsJsonObject().get("name").getAsString()).append("** (");
            s.append(jsonElement.getAsJsonObject().get("symbol").getAsString()).append(")\t\t");
            s.append(jsonElement.getAsJsonObject().get("price_usd").getAsString()).append(" USD\t\t");
            s.append(jsonElement.getAsJsonObject().get("price_btc").getAsString()).append(" BTC\t\t");
            s.append(jsonElement.getAsJsonObject().get("percent_change_24h").getAsString()).append("% (24h)");
            s.append("\n");
        });
        normal.addField(kyoko.getI18n().get(l, "cryptotop.title"), s.toString(), false);
        message.getTextChannel().sendMessage(normal.build()).queue();
    }
}
