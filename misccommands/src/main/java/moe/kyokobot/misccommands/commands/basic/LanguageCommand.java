package moe.kyokobot.misccommands.commands.basic;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;

public class LanguageCommand extends Command {

    private final DatabaseManager databaseManager;

    public LanguageCommand(DatabaseManager databaseManager) {
        name = "language";
        aliases = new String[] {"locale", "lang"};
        category = CommandCategory.BASIC;

        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.hasArgs()) {
            for (Language l : Language.values()) {
                if (l == Language.DEFAULT) continue;

                if (l.name().equalsIgnoreCase(context.getConcatArgs())
                        || l.getShortName().equalsIgnoreCase(context.getConcatArgs())
                        || l.getEmoji().equalsIgnoreCase(context.getConcatArgs())
                        || l.getLocalized().equalsIgnoreCase(context.getConcatArgs())) {
                    try {
                        UserConfig uc = databaseManager.getUser(context.getSender());
                        uc.setLanguage(l);
                        databaseManager.save(uc);
                        context.send(CommandIcons.SUCCESS + String.format(context.getTranslated("language.set"), l.getLocalized()));
                    } catch (Exception e) {
                        CommonErrors.exception(context, e);
                        logger.error("Error saving language!", e);
                        Sentry.capture(e);
                    }
                    return;
                }
            }
        }

        languageList(context);
    }

    private void languageList(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();

        StringBuilder langs = new StringBuilder();

        for (Language l : Language.values()) {
            if (l == Language.DEFAULT) continue;

            if (l == Language.ENGLISH || !context.getI18n().get(l, "language.name").equals("English (US)"))
                langs.append("`").append(context.getPrefix()).append("lang ").append(l.getShortName())
                        .append("` - ").append(l.getEmoji()).append(" ").append(l.getLocalized()).append("\n");
        }

        eb.setTitle("Select language");
        eb.setDescription(langs.toString());
        context.send(eb.build());
    }
}
