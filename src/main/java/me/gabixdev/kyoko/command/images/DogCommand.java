package me.gabixdev.kyoko.command.images;

import com.google.gson.JsonObject;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class DogCommand extends Command
{
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"dog"};
    public DogCommand(Kyoko kyoko)
    {
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
        return CommandType.IMAGES;
    }

    @Override
    public String getDescription() {
        return "dog.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        try {
            JsonObject jsonObject = GsonUtil.fromStringToJsonElement(URLUtil.readUrl("https://dog.ceo/api/breeds/image/random")).getAsJsonObject();
            if(!jsonObject.get("status").getAsString().equals("success"))
            {
                EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "dog.error"), false);
                message.getChannel().sendMessage(builder.build()).queue();
                return;
            }
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            builder.addField(kyoko.getI18n().get(l, "dog.title"), kyoko.getI18n().get(l, "dog.subtitle"), true);
            builder.setImage(jsonObject.get("message").getAsString());
            message.getChannel().sendMessage(builder.build()).queue();
        } catch (Exception ex) {
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "dog.error"), false);
            message.getChannel().sendMessage(builder.build()).queue();
            ex.printStackTrace();
        }
    }
}
