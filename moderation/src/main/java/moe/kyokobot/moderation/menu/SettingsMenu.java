package moe.kyokobot.moderation.menu;

import de.vandermeer.asciitable.AsciiTable;
import io.sentry.Sentry;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.*;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("squid:S3776")
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
        String autoRole = guildConfig.getModerationConfig().getAutoRole();
        Role r = null;
        if (autoRole != null && !autoRole.isEmpty())
            r = context.getGuild().getRoleById(autoRole);

        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow(context.getTranslated("settings.title"));
        at.addRule();
        at.addRow("[1] " + context.getTranslated("settings.language") + ": " + guildConfig.getLanguage().getLocalized());
        at.addRow("[2] " + context.getTranslated("settings.levelupmessages") + ": " +
                StringUtil.toggleFormat(context, guildConfig.getModerationConfig().isLevelupMessages()));
        at.addRow("[3] " + context.getTranslated("settings.autorole") + ": "
                + (r == null ? context.getTranslated("generic.none") : r.getName() + " (" + r.getId() + ")"));
        at.addRow("");
        List<String> footerLines = StringUtil.splitString(context.getTranslated("settings.footerhelp"), 55);
        footerLines.forEach(line -> at.addRow("// " + line));
        at.addRule();

        context.send("```less\n" + at.render(60) + "\n```", message -> {
            MessageWaiter waiter = new MessageWaiter(eventWaiter, context);
            waiter.setMessageHandler(event -> {
                message.delete().queue();
                handleMainMessage(event);
            });
            waiter.setTimeoutHandler(() -> onTimeout(message));
            waiter.create();
        });
    }

    private void handleMainMessage(MessageReceivedEvent event) {
        try {
            switch (event.getMessage().getContentRaw().trim()) {
                case "1":
                    triedAgain = false;
                    renderLang();
                    break;
                case "2":
                    triedAgain = false;
                    guildConfig = databaseManager.getGuild(context.getGuild());
                    guildConfig.getModerationConfig().setLevelupMessages(!guildConfig.getModerationConfig().isLevelupMessages());
                    databaseManager.save(guildConfig);
                    context.send(CommandIcons.SUCCESS + context.getTranslated("settings.levelupmessages." + (guildConfig.getModerationConfig().isLevelupMessages() ? "enabled" : "disabled")));
                    break;
                case "3":
                    if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
                        context.send(CommandIcons.ERROR + context.getTranslated("settings.autorole.nopermission"));
                        return;
                    }
                    triedAgain = false;
                    renderRole();
                    break;
                case "exit":
                    break;
                default:
                    context.send(CommandIcons.ERROR + context.getTranslated("settings.invalid"));
                    if (!triedAgain) {
                        triedAgain = true;
                        renderMain();
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error("Error handling main menu message!", e);
            CommonErrors.exception(context, e);
            Sentry.capture(e);
        }
    }

    private void renderLang() {
        context.send(CommandIcons.INFO + context.getTranslated("settings.language.question"), message -> {
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
                        context.send(CommandIcons.ERROR + context.getTranslated("settings.invalid"));
                        if (!triedAgain) {
                            triedAgain = true;
                            renderLang();
                        }
                        break;
                }
            });
            waiter.setTimeoutHandler(() -> onTimeout(message));
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

        eb.setTitle(context.getTranslated("settings.language.set"));
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

                    guildConfig = databaseManager.getGuild(context.getGuild());
                    guildConfig.setLanguage(l);
                    databaseManager.save(guildConfig);
                    context.send(CommandIcons.SUCCESS + String.format(context.getTranslated("settings.language.set"), l.getLocalized()));
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

    private void renderRole() {
        context.send(CommandIcons.INFO + context.getTranslated("settings.autorole.question"), message -> {
            MessageWaiter waiter = new MessageWaiter(eventWaiter, context);
            waiter.setMessageHandler(event -> {
                message.delete().queue();
                String content = event.getMessage().getContentRaw().trim();
                if (content.equalsIgnoreCase("none")) {
                    setRole(null);
                    return;
                }

                if (!content.equalsIgnoreCase("exit")) {
                    try {
                        Role r = event.getGuild().getRoleById(content);
                        setRole(r);
                    } catch (IllegalArgumentException e) {
                        List<Role> found = event.getGuild().getRoles().stream().filter(role ->
                                role.getAsMention().equals(content)
                                        || role.getName().equalsIgnoreCase(content)
                                        || role.getName().toLowerCase().contains(content.toLowerCase()))
                                .collect(Collectors.toList());

                        if (found.isEmpty()) {
                            context.send(String.format(context.getTranslated("settings.autorole.notfound"), content));
                            if (!triedAgain) {
                                triedAgain = true;
                                renderRole();
                            }
                        } else if (found.size() == 1) {
                            setRole(found.get(0));
                        } else {
                            selectRole(found);
                        }
                    } catch (Exception e) {
                        logger.error("Error setting guild language!", e);
                        Sentry.capture(e);
                    }
                }
            });
            waiter.setTimeoutHandler(() -> onTimeout(message));
            waiter.create();
        });
    }

    private void selectRole(List<Role> roles) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommandIcons.INFO).append(context.getTranslated("settings.autorole.select")).append("\n\n");

        for (int i = 0; i < 10; i++) {
            if (i == roles.size()) break;
            sb.append("**").append(i + 1).append(".** ").append(roles.get(i).getName()).append(" `(").append(roles.get(i).getId()).append(")`\n");
        }

        context.send(sb.toString(), message -> {
            MessageWaiter waiter = new MessageWaiter(eventWaiter, context);
            waiter.setMessageHandler(event -> {
                message.delete().queue();

                String content = event.getMessage().getContentRaw();
                if (!content.equalsIgnoreCase("exit")) {
                    try {
                        int i = Integer.parseUnsignedInt(content);

                        if (i > roles.size() || i > 10 || i == 0) {
                            context.send(CommandIcons.ERROR + context.getTranslated("settings.invalid"));
                            if (!triedAgain) {
                                triedAgain = true;
                                selectRole(roles);
                            }
                            return;
                        }

                        setRole(roles.get(i - 1));
                    } catch (NumberFormatException e) {
                        message.delete().queue();
                        CommonErrors.notANumber(context, content);
                        if (!triedAgain) {
                            triedAgain = true;
                            selectRole(roles);
                        }
                    }
                }
            });

            waiter.setTimeoutHandler(() -> onTimeout(message));
            waiter.create();
        });
    }

    private void setRole(Role r) {
        if (r == null) {
            guildConfig.getModerationConfig().setAutoRole("");
            databaseManager.save(guildConfig);
            context.send(CommandIcons.SUCCESS + context.getTranslated("settings.autorole.disabled"));
        } else {
            guildConfig.getModerationConfig().setAutoRole(r.getId());
            databaseManager.save(guildConfig);
            context.send(CommandIcons.SUCCESS +
                    String.format(context.getTranslated("settings.autorole.set"), r.getName() + " (" + r.getId() + ")"));
        }
    }

    private void onTimeout(Message message) {
        if (langListMessage != null) langListMessage.delete().queue();
        if (message != null) message.delete().queue();
        context.send(CommandIcons.ERROR + context.getTranslated("settings.timeout"));
    }
}