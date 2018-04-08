package me.gabixdev.kyoko.command.images;

import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class CatCommand extends Command {

    private final String[] aliases = new String[]{"cat"};
    private Kyoko kyoko;

    public CatCommand(Kyoko kyoko) {
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
        return "cat.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGES;
    }

    @Override
    public String getUsage() {
        return "cat.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        try {
            Image image = kyoko.getWeeb4j().getRandomImage("animal_cat", HiddenMode.DEFAULT, message.getTextChannel().isNSFW() ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();
            //String url = GsonUtil.fromStringToJsonElement(URLUtil.readUrl("http://random.cat/meow")).getAsJsonObject().get("file").getAsString();
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            builder.addField(kyoko.getI18n().get(l, "cat.title"), Constants.POWERED_BY_WEEB, true);
            builder.setImage(image.getUrl());
            message.getChannel().sendMessage(builder.build()).queue();
        } catch (Exception ex) {
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "cat.error"), false);
            message.getChannel().sendMessage(builder.build()).queue();
            ex.printStackTrace();
        }
    }
}
