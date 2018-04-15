package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.TranslationUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class LangCommand extends Command {
    private Kyoko kyoko;
    private final String langoptions;

    public LangCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.aliases = new String[] {"language", "lang", "locale"};
        this.label = aliases[0];
        this.category = CommandCategory.BASIC;
        this.description = "lang.description";

        StringBuilder langs = new StringBuilder();
        for (Language l : Language.values()) {
            langs.append("`").append(kyoko.getSettings().getPrefix()).append("lang ").append(l.getShortName()).append("` - ").append(l.getEmoji()).append(" ").append(l.getLocalized()).append(" translated in ").append(TranslationUtil.getTranslationCompleteness(l)).append("%\n");
        }
        langs.append("\nCan't find your language? [Teach Kyoko it by clicking here ;3](https://poeditor.com/join/project/SUP7N8fDk0)");
        langoptions = langs.toString();
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        if (args.length == 1) {
            eb.addField("Select language:", langoptions, false);
            message.getChannel().sendMessage(eb.build()).queue();
        } else {
            for (Language l : Language.values()) {
                if (l.getShortName().equalsIgnoreCase(args[1])
                        || l.getEmoji().equalsIgnoreCase(args[1])
                        || l.getLocalized().equalsIgnoreCase(args[1])) {
                    UserConfig uc = kyoko.getDatabaseManager().getUser(message.getAuthor());
                    uc.language = l;
                    kyoko.getDatabaseManager().saveUser(message.getAuthor(), uc);
                    message.getChannel().sendMessage(String.format(kyoko.getI18n().get(l, "language.set"), l.getLocalized())).queue();
                    return;
                }
            }
            eb.addField("Select language:", langoptions, false);
            message.getChannel().sendMessage(eb.build()).queue();
        }
    }
}