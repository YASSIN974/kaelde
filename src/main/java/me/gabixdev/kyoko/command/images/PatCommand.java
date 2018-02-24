package me.gabixdev.kyoko.command.images;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

import java.util.ArrayList;
import java.util.List;

public class PatCommand extends Command {
    private static final String[] aliases = new String[]{"pat"};
    private Kyoko kyoko;

    public PatCommand(Kyoko kyoko) {
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
        return "pat.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.IMAGES;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (args.length == 1) {
            normal.setTitle(kyoko.getI18n().get(l, "pat.description"));
        } else {
            boolean skipme = false;

            if (message.getContentRaw().startsWith(kyoko.getJda().getSelfUser().getAsMention())) {
                if (StringUtil.getOccurencies(message.getContentRaw(), kyoko.getJda().getSelfUser().getAsMention()) == 1)
                    skipme = true;
            }

            if (message.getMentionedUsers().isEmpty()) {
                normal.setTitle(String.format(kyoko.getI18n().get(l, "pat.someone"), message.getAuthor().getName(), StringUtil.stripPrefix(kyoko, args[0], message.getContentRaw())));
            } else {
                List<String> userlist = new ArrayList<>();
                for (User u : message.getMentionedUsers()) {
                    if (skipme)
                        if (u.getIdLong() == kyoko.getJda().getSelfUser().getIdLong())
                            continue;
                    userlist.add(u.getName());
                }
                normal.setTitle(String.format(kyoko.getI18n().get(l, "pat.someone"), message.getAuthor().getName(), String.join(", ", userlist)));
            }
        }

        String url = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(NekosCommand.NEKOS_URL + "pat")).getAsJsonObject().get("url").getAsString();

        normal.setImage(url);

        message.getTextChannel().sendMessage(normal.build()).queue();
    }
}

