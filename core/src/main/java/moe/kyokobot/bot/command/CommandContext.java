package moe.kyokobot.bot.command;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandContext {
    private Settings settings;
    private I18n i18n;
    private final Command command;
    private final Language language;
    private final MessageReceivedEvent event;
    private final String prefix;
    private final String label;
    private final String concatArgs;
    private final String[] args;

    public CommandContext(Settings settings, I18n i18n, Command command, MessageReceivedEvent event, String prefix, String label, String concatArgs, String[] args) {
        this.settings = settings;
        this.i18n = i18n;
        this.command = command;
        this.prefix = prefix;
        if (event.getChannelType().isGuild()) this.language = i18n.getLanguage(event.getMember());
        else this.language = i18n.getLanguage(event.getAuthor());
        this.event = event;
        this.label = label;
        this.concatArgs = concatArgs.trim();
        this.args = args;
    }

    public Command getCommand() {
        return command;
    }

    public User getSender() {
        return event.getAuthor();
    }

    public Member getMember() {
        return event.getMember();
    }

    public TextChannel getChannel() {
        return event.getTextChannel();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }

    public Language getLanguage() {
        return language;
    }

    public String getConcatArgs() {
        return concatArgs;
    }

    public boolean hasArgs() {
        return !concatArgs.isEmpty();
    }

    public String skipConcatArgs(int n) {
        return Arrays.stream(args).skip(n).collect(Collectors.joining(" "));
    }

    public Settings getSettings() {
        return settings;
    }

    public I18n getI18n() {
        return i18n;
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public void send(CharSequence message) {
        send(message, null);
    }

    public void send(MessageEmbed message) {
        send(message, null);
    }

    public void send(CharSequence message, Consumer<Message> callback) {
        event.getChannel().sendMessage(message).queue(callback);
    }

    // use this method only for debug commands to prevent theoretical token guessing
    public void sendChecked(CharSequence message, Consumer<Message> callback) {
        if (checkSensitive(message.toString())) {
            event.getChannel().sendMessage(CommandIcons.error + getTranslated("generic.sensitive")).queue(callback);
        } else {
            event.getChannel().sendMessage(message).queue(callback);
        }
    }

    public void send(MessageEmbed message, Consumer<Message> callback) {
        event.getChannel().sendMessage(message).queue();
    }

    public String getTranslated(String key) {
        return i18n.get(language, key);
    }

    public EmbedBuilder getNormalEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(getNormalColor());
        //eb.setFooter(  "kyokobot v" + Constants.VERSION + " | created by gabixdev & contributors", null);
        return eb;
    }

    public Color getNormalColor() {
        Color c = settings.bot.normalColor;

        if (event.getMember() != null) {
            if (event.getMember().getColor() != null) {
                c = event.getMember().getColor();
            }
        }

        return c;
    }

    public EmbedBuilder getErrorEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(settings.bot.errorColor);
        eb.setFooter(settings.bot.botName + " v" + Constants.VERSION + " | created by gabixdev & contributors", null);
        return eb;
    }

    public boolean checkSensitive(String input) {
        if (input.contains(settings.connection.token)) return true;
        return settings.apiKeys.values().stream().anyMatch(input::contains);
    }
}
