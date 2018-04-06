package me.gabixdev.kyoko.command.images;

import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class TrapCommand extends Command
{
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"trap"};

    public TrapCommand(Kyoko kyoko)
    {
        this.kyoko = kyoko;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getUsage() {
        return "trap.usage";
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
        return "trap.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        Image image = kyoko.getWeeb4j().getRandomImage("trap", HiddenMode.DEFAULT, message.getTextChannel().isNSFW() ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        eb.addField("Trap", "powered by weeb.sh", false);
        eb.setImage(image.getUrl());
        message.getChannel().sendMessage(eb.build()).queue();
    }
}
