package me.gabixdev.kyoko.command.images;

import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import com.google.gson.JsonObject;
import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DogCommand extends Command
{
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"dog", "doggo", "doge", "hund", "piesek"};
    private final List<String> breeds = new ArrayList<>();
    private final String dogUrl = "https://dog.ceo/api/breed/%s/images/random";
    private String cachedBreeds = "";
    public DogCommand(Kyoko kyoko)
    {
        this.kyoko = kyoko;
        try {
            JsonObject breed = GsonUtil.fromStringToJsonElement(URLUtil.readUrl("https://dog.ceo/api/breeds/list/all")).getAsJsonObject().get("message").getAsJsonObject();
            breeds.addAll(breed.keySet());
            cachedBreeds = "`" + String.join("`, `", breeds) + "`";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getUsage() {
        return "dog.usage";
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGES;
    }

    @Override
    public String getDescription() {
        return "dog.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        DogResponse dr;

        if (args.length == 1) {
            try {
                if (message.getIdLong() % 2 == 0) {
                    dr = getWeeb(message.getTextChannel().isNSFW());
                } else {
                    try {
                        dr = getDogCeo(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dr = getWeeb(message.getTextChannel().isNSFW());
                    }
                }

                EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                builder.addField(String.format(kyoko.getI18n().get(l, "dog.title"), dr.breed), dr.src, true);
                builder.setImage(dr.url);
                message.getChannel().sendMessage(builder.build()).queue();
            } catch (Exception e) {
                e.printStackTrace();
                CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
            }
        } else {
            if (args[1].equalsIgnoreCase("list") || !breeds.contains(args[1].toLowerCase())) {
                EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                builder.addField(String.format(kyoko.getI18n().get(l, "dog.title"), ""), String.format(kyoko.getI18n().get(l, "dog.breedlist"), cachedBreeds), false);
                message.getTextChannel().sendMessage(builder.build()).queue();
                return;
            }

            try {
                dr = getDogCeo(args[1].toLowerCase());
                EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                builder.addField(String.format(kyoko.getI18n().get(l, "dog.title"), dr.breed), Constants.POWERED_BY_DOGCEO, true);
                builder.setImage(dr.url);
                message.getChannel().sendMessage(builder.build()).queue();
            } catch (Exception e) {
                e.printStackTrace();
                CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
            }
        }
    }

    private DogResponse getWeeb(boolean nsfw) throws Exception {
        Image image = kyoko.getWeeb4j().getRandomImage("animal_dog", HiddenMode.DEFAULT, nsfw ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();
        if (image == null) throw new IOException("API Error");

        DogResponse dr = new DogResponse();
        dr.url = image.getUrl();
        dr.src = Constants.POWERED_BY_WEEB;
        return dr;
    }

    private DogResponse getDogCeo(String breed) throws Exception {
        if (breed == null) breed = breeds.get(RandomUtils.nextInt(0, breeds.size()));

        JsonObject imgObject = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(String.format(dogUrl, breed))).getAsJsonObject();
        if (imgObject.get("status").getAsString().equals("success")) {
            DogResponse dr = new DogResponse();
            dr.breed = "(" + breed + ")";
            dr.url = imgObject.get("message").getAsString();
            dr.src = Constants.POWERED_BY_DOGCEO;
            return dr;
        } else {
            throw new IOException("API Error");
        }
    }

    private class DogResponse {
        public String url;
        public String src;
        public String breed = "";
    }
}
