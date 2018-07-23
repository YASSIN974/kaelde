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
import org.jetbrains.annotations.NotNull;

public class LanguageCommand extends Command {

    private final DatabaseManager databaseManager;

    public LanguageCommand(DatabaseManager databaseManager) {
        name = "language";
        aliases = new String[] {"locale", "lang"};
        category = CommandCategory.BASIC;

        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        try {
            if (context.hasArgs()) {
                if (context.getConcatArgs().equalsIgnoreCase("default")) {
                    UserConfig uc = databaseManager.getUser(context.getSender());
                    uc.setLanguage(Language.DEFAULT);
                    databaseManager.save(uc);
                    context.send(CommandIcons.SUCCESS + context.getI18n().get(context.getI18n().getLanguage(context.getGuild()), "language.set"));
                    return;
                } else for (Language l : Language.values()) {
                    if (l == Language.DEFAULT) continue;

                    if (l.name().equalsIgnoreCase(context.getConcatArgs())
                            || l.getShortName().equalsIgnoreCase(context.getConcatArgs())
                            || l.getEmoji().equalsIgnoreCase(context.getConcatArgs())
                            || l.getLocalized().equalsIgnoreCase(context.getConcatArgs())) {

                        UserConfig uc = databaseManager.getUser(context.getSender());
                        uc.setLanguage(l);
                        databaseManager.save(uc);
                        context.send(CommandIcons.SUCCESS + context.getI18n().get(l, "language.set"));
                        return;
                    }
                }
            }

            languageList(context);
        } catch (Exception e) {
            CommonErrors.exception(context, e);
            logger.error("Error saving language!", e);
            Sentry.capture(e);
        }
    }

    private void languageList(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();

        StringBuilder langs = new StringBuilder();

        for (Language l : Language.values()) {
            if (l == Language.DEFAULT) {
                langs.append("`").append(context.getPrefix()).append("lang default` - Use guild defaults\n");
                continue;
            }

            if (l == Language.ENGLISH || !context.getI18n().get(l, "language.name").equals("English (US)"))
                langs.append("`").append(context.getPrefix()).append("lang ").append(l.getShortName())
                        .append("` - ").append(l.getEmoji()).append(" ").append(l.getLocalized()).append("\n");
        }

        langs.append("\n[Translate Kyoko](https://crwd.in/kyoko)\nIf you want to change guild language use `").append(context.getPrefix()).append("settings`");
        eb.setTitle("Select language");
        eb.setDescription(langs.toString());
        context.send(eb.build());
    }
}
