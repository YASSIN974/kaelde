package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class LangCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"language", "lang"};
    private final String langoptions;

    public LangCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        StringBuilder langs = new StringBuilder();
        for (Language l : Language.values()) {
            langs.append("`").append(kyoko.getSettings().getPrefix()).append("lang ").append(l.getShortName()).append("` - ").append(l.getEmoji()).append(" ").append(l.getLocalized()).append("\n");
        }
        langoptions = langs.toString();
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
        return "lang.description";
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