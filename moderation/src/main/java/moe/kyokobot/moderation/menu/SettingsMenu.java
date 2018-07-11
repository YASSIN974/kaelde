package moe.kyokobot.moderation.menu;

import de.vandermeer.asciitable.AsciiTable;
import io.sentry.Sentry;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.*;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SettingsMenu {
    private static final Logger logger = LoggerFactory.getLogger(SettingsMenu.class);

    private final EventWaiter eventWaiter;
    private final DatabaseManager databaseManager;
    private final CommandContext context;

    private Message langListMessage = null;
    private GuildConfig guildConfig;
    private boolean triedAgain = false;

    public SettingsMenu(EventWaiter eventWaiter, DatabaseManager databaseManager, CommandContext context) {
        this.eventWaiter = eventWaiter;
        this.databaseManager = databaseManager;
        this.context = context;
    }

    public void create() {
        try {
            guildConfig = databaseManager.getGuild(context.getGuild());
            renderMain();
        } catch (Exception e) {
            logger.error("Error creating settings menu!", e);
            CommonErrors.exception(context, e);
            Sentry.capture(e);
        }
    }

    private void renderMain() {
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow(context.getTranslated("settings.title"));
        at.addRule();
        at.addRow("[1] " + context.getTranslated("settings.language") + ": " + guildConfig.getLanguage().getLocalized());
        at.addRow("[2] " + context.getTranslated("settings.levelupmessages") + ": " +
                StringUtil.toggleFormat(context, guildConfig.getModerationConfig().isLevelupMessages()));
        at.addRow("[3] Auto-role: (none)");
        //at.addRow("[4] Voteskip: disabled");
        at.addRow("");
        List<String> footerLines = StringUtil.splitString(context.getTranslated("menu.footerhelp"), 55);
        footerLines.forEach(line -> at.addRow("// " + line));
        at.addRule();

        context.send("```less\n" + at.render(60) + "\n```", message -> {
            MessageWaiter waiter = new MessageWaiter(eventWaiter, context);
            waiter.setMessageHandler(event -> {
                message.delete().queue();
                handleMainMessage(event);
            });
            waiter.setTimeoutHandler(() -> {
                message.delete().queue();
                context.send(CommandIcons.ERROR + "Operation cancelled due to no response!");
            });
            waiter.create();
        });
    }

    private void handleMainMessage(MessageReceivedEvent event) {
        switch (event.getMessage().getContentRaw().trim()) {
            case "1":
                triedAgain = false;
                renderLang();
                break;
            case "2":
                triedAgain = false;
                guildConfig.getModerationConfig().setLevelupMessages(!guildConfig.getModerationConfig().isLevelupMessages());

                break;
            case "exit":
                break;
            default:
                context.send(CommandIcons.ERROR + "Invalid option!");
                if (!triedAgain) {
                    triedAgain = true;
                    renderMain();
                }
                break;
        }
    }

    private void renderLang() {
        context.send(CommandIcons.INFO + "Enter language code (eg. `en`), name (eg. `English`), `list` to display language list or `exit` to leave this menu.", message -> {
            MessageWaiter waiter = new MessageWaiter(eventWaiter, context);
            waiter.setMessageHandler(event -> {
                if (langListMessage != null) langListMessage.delete().queue();
                message.delete().queue();
                String content = event.getMessage().getContentRaw().trim();

                if (setLanguage(content)) return;

                switch (content.toLowerCase()) {
                    case "exit":
                        return;
                    case "list":
                        triedAgain = false;
                        renderLangList();
                        renderLang();
                        return;
                    default:
                        context.send(CommandIcons.ERROR + "Invalid option!");
                        if (!triedAgain) {
                            triedAgain = true;
                            renderLang();
                        }
                        break;
                }
            });
            waiter.setTimeoutHandler(() -> {
                if (langListMessage != null) langListMessage.delete().queue();
                message.delete().queue();
                context.send(CommandIcons.ERROR + "Operation cancelled due to no response!");
            });
            waiter.create();
        });
    }

    private void renderLangList() {
        EmbedBuilder eb = context.getNormalEmbed();

        StringBuilder langs = new StringBuilder();

        for (Language l : Language.values()) {
            if (l != Language.DEFAULT && (l == Language.ENGLISH || !context.getI18n().get(l, "language.name").equals("English (US)")))
                langs.append(l.getEmoji()).append(" ").append(l.getLocalized())
                        .append(" - `").append(l.getShortName()).append("`\n");
        }

        eb.setTitle("Language list");
        eb.setDescription(langs.toString());
        context.send(eb.build(), message -> langListMessage = message);
    }

    private boolean setLanguage(String content) {
        try {
            for (Language l : Language.values()) {
                if (l == Language.DEFAULT) continue;

                if (l.name().equalsIgnoreCase(content)
                        || l.getShortName().equalsIgnoreCase(content)
                        || l.getEmoji().equalsIgnoreCase(content)
                        || l.getLocalized().equalsIgnoreCase(content)) {

                    guildConfig.setLanguage(l);
                    databaseManager.save(guildConfig);
                    context.send(CommandIcons.SUCCESS + "Guild language changed to: `" + l.getLocalized() + "`");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Error setting guild language!", e);
            Sentry.capture(e);
            return true;
        }
        return false;
    }
}