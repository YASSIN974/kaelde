package me.gabixdev.kyoko.command.images;

import com.google.gson.JsonObject;
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
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DogCommand extends Command
{
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"dog"};
    private final List<String> breeds = new ArrayList<>();
    private final String dogUrl = "https://dog.ceo/api/breed/%s/images/random";
    public DogCommand(Kyoko kyoko)
    {
        this.kyoko = kyoko;
        try {
            JsonObject breed = GsonUtil.fromStringToJsonElement(URLUtil.readUrl("https://dog.ceo/api/breeds/list/all")).getAsJsonObject().get("message").getAsJsonObject();

            for(String object: breed.keySet())
            {
                breeds.add(object);
            }
            System.out.println(breeds);
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
    public CommandType getType() {
        return CommandType.IMAGES;
    }

    @Override
    public String getDescription() {
        return "dog.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (args.length == 1) {
            String breed = breeds.get(RandomUtils.nextInt(0, breeds.size()));
            JsonObject imgObject = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(String.format(dogUrl, breed))).getAsJsonObject();
            if (imgObject.get("status").getAsString().equals("success"))
            {
                String imgUrl = imgObject.get("message").getAsString();
                EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                builder.addField(String.format(kyoko.getI18n().get(l, "dog.title"), "(" + breed + ")"), kyoko.getI18n().get(l, "dog.subtitle"), true);
                builder.setImage(imgUrl);
                message.getChannel().sendMessage(builder.build()).queue();
            }
            else
            {
                CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
            }

        }
        if(args[1].equalsIgnoreCase("list"))
        {
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            List<String> breedList = new ArrayList<>();
            for(String breed : breeds)
            {
                breedList.add("`" + breed + "`");
            }
            String bL = String.join(", ", breedList);
            builder.addField(String.format(kyoko.getI18n().get(l, "dog.title"), ""), String.format(kyoko.getI18n().get(l, "dog.breedlist"), bL), false);
            message.getTextChannel().sendMessage(builder.build()).queue();
            return;
        }
        if (!breeds.contains(args[1].toLowerCase())) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }
        JsonObject imgObject = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(String.format(dogUrl, args[1].toLowerCase()))).getAsJsonObject();
        if (imgObject.get("status").getAsString().equals("success")) {
            String imgUrl = imgObject.get("message").getAsString();
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            builder.addField(String.format(kyoko.getI18n().get(l, "dog.title"), "(" + args[1].toLowerCase() + ")"), kyoko.getI18n().get(l, "dog.subtitle"), true);
            builder.setImage(imgUrl);
            message.getChannel().sendMessage(builder.build()).queue();
        }
        else
        {
            CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
        }
    }
}
