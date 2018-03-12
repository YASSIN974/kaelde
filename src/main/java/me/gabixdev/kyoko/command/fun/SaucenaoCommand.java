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

import java.io.IOException;

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
        if(args.length < 2 && message.getAttachments().isEmpty()) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }
        EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        builder.addField(kyoko.getI18n().get(l, "saucenao.title"), kyoko.getI18n().get(l, "saucenao.searching"), false);
        message.getTextChannel().sendMessage(builder.build()).queue(success -> {
        try {
            builder.clearFields();
            String imgUrl = args.length >= 2 ? args[1] : message.getAttachments().get(0).getUrl();
            JsonObject jsonObject = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(String.format(urlFormat, kyoko.getSettings().getSaucenaoApiKey(), imgUrl))).getAsJsonObject();
            JsonArray results = jsonObject.get("results").getAsJsonArray();
            int res = 1;
            builder.addField(kyoko.getI18n().get(l, "saucenao.title"), kyoko.getI18n().get(l, "saucenao.subtitle"), false);
            for(JsonElement result : results) {
                JsonObject resultt = result.getAsJsonObject();
                JsonObject data = resultt.get("data").getAsJsonObject();
                String title = resultt.get("data").getAsJsonObject().keySet().contains("title") ? kyoko.getI18n().get(l, "saucenao.image") + " - " + data.get("title").getAsString() : "Anime - " + data.get("source").getAsString();
                String similarity = resultt.get("header").getAsJsonObject().get("similarity").getAsString() + "%";
                builder.addField(res++ + ".",  "[" + title + "](" + (data.keySet().contains("ext_urls") ? data.get("ext_urls").getAsJsonArray().get(0).getAsString() : resultt.get("header").getAsJsonObject().get("thumbnail").getAsString()) + ") - " + String.format(kyoko.getI18n().get(l, "saucenao.similarity"), similarity), false);
            }
            success.editMessage(builder.build()).queue();

        } catch (JsonParseException e) {
            EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            embedBuilder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "saucenao.error"), false);
            success.editMessage(embedBuilder.build()).queue();
        } catch (IOException e) {
            success.delete().queue();
            CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
        }});

    }
}
