package me.gabixdev.kyoko.command.images;

import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

import java.util.ArrayList;
import java.util.List;

public class LickCommand extends Command {
    private static final String[] aliases = new String[]{"lick"};
    private Kyoko kyoko;

    public LickCommand(Kyoko kyoko) {
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
        return "lick.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGES;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (args.length == 1) {
            normal.addField(kyoko.getI18n().get(l, "lick.description"), Constants.POWERED_BY_WEEB, false);
        } else {
            boolean skipme = false;

            if (message.getContentRaw().startsWith(kyoko.getJda().getSelfUser().getAsMention())) {
                if (StringUtil.getOccurencies(message.getContentRaw(), kyoko.getJda().getSelfUser().getAsMention()) == 1)
                    skipme = true;
            }

            if (message.getMentionedUsers().isEmpty()) {
                normal.addField(String.format(kyoko.getI18n().get(l, "lick.someone"), message.getAuthor().getName(), StringUtil.stripPrefix(kyoko, args[0], message.getContentRaw())), Constants.POWERED_BY_WEEB, false);
            } else {
                List<String> userlist = new ArrayList<>();
                for (User u : message.getMentionedUsers()) {
                    if (skipme)
                        if (u.getIdLong() == kyoko.getJda().getSelfUser().getIdLong())
                            continue;
                    userlist.add(u.getName());
                }
                normal.addField(String.format(kyoko.getI18n().get(l, "lick.someone"), message.getAuthor().getName(), String.join(", ", userlist)), Constants.POWERED_BY_WEEB, false);
            }
        }

        Image image = kyoko.getWeeb4j().getRandomImage("lick", HiddenMode.DEFAULT, message.getTextChannel().isNSFW() ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();

        normal.setImage(image.getUrl());

        message.getTextChannel().sendMessage(normal.build()).queue();
    }
}

