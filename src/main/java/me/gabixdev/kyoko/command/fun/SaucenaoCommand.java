package me.gabixdev.kyoko.command.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import sun.rmi.server.InactiveGroupException;

import javax.naming.directory.InvalidAttributesException;
import java.util.Iterator;
import java.util.Map;

public class SaucenaoCommand extends Command {

    private final String[] aliases = new String[] {"saucenao", "sauce"};
    private Kyoko kyoko;
    private final String urlFormat = "https://saucenao.com/search.php?db=999&output_type=2&numres=5&api_key=%s&url=%s";

    public SaucenaoCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public String getDescription() {
        return "saucenao.description";
    }

    @Override
    public String getUsage() {
        return "saucenao.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        if(args.length < 2) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }
        String imgUrl = args[1];
        try {
            JsonObject jsonObject = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(String.format(urlFormat, kyoko.getSettings().getSaucenaoApiKey(), imgUrl))).getAsJsonObject();
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            JsonArray results = jsonObject.get("results").getAsJsonArray();
            int res = 1;
            builder.addField(kyoko.getI18n().get(l, "saucenao.title"), kyoko.getI18n().get(l, "saucenao.subtitle"), false);
            for(JsonElement result : results) {
                JsonObject resultt = result.getAsJsonObject();
                JsonObject data = resultt.get("data").getAsJsonObject();
                String title = resultt.get("data").getAsJsonObject().keySet().contains("title") ? "Image - " + data.get("title").getAsString() : "Anime - " + data.get("source").getAsString();
                builder.addField(res++ + ".",  "[" + title + "](" + data.get("ext_urls").getAsJsonArray().get(0).getAsString() + ") \n", false);
            }
            message.getTextChannel().sendMessage(builder.build()).queue();

        } catch (JsonParseException e) {
            CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
        }

    }
}
