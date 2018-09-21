package moe.kyokobot.bot.command;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.Globals;
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
    private I18n i18n;
    private final Command command;
    private final Language language;
    private final MessageReceivedEvent event;
    private final String prefix;
    private final String label;
    private final String concatArgs;
    private final String[] args;

    public CommandContext(I18n i18n, Command command, MessageReceivedEvent event, String prefix, String label, String concatArgs, String[] args) {
        this.i18n = i18n;
        this.command = command;
        this.prefix = prefix;
        if (event.getChannelType().isGuild()) this.language = i18n.getLanguage(event.getMember());
        else this.language = i18n.getLanguage(event.getAuthor());
        this.event = event;
        this.label = label;
        this.concatArgs = concatArgs.trim();
        this.args = args;

        if (Globals.inKyokoServer) CommandIcons.loadKyokoIcons();
    }

    /**
     * Gets Command object from current command context.
     * @return The command object of current context.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Gets the user who sent a message triggering the command.
     * @return The user who invoked the command.
     */
    public User getSender() {
        return event.getAuthor();
    }

    /**
     * Gets the member of current guild who invoked the command.
     * @return The member of current guild.
     */
    public Member getMember() {
        return event.getMember();
    }

    /**
     * Gets bot user as guild member the command was invoked from.
     * @return The bot member of current guild.
     */
    public Member getSelfMember() {
        return event.getGuild().getSelfMember();
    }

    /**
     * Gets the text channel where the command was invoked.
     * @return The channel where the command was invoked.
     */
    public TextChannel getChannel() {
        return event.getTextChannel();
    }

    /**
     * Gets the guild where the command was invoked.
     * @return The guild where the command was invoked
     */
    public Guild getGuild() {
        return event.getGuild();
    }

    /**
     * Gets original message received event handled by command manager.
     * @return The MessageReceivedEvent object which triggered the command.
     */
    public MessageReceivedEvent getEvent() {
        return event;
    }

    /**
     * Gets the prefix which was used to trigger the command.
     * @return The prefix which was used to trigger the command.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the command label which was used to trigger the command.
     * @return The command label which was used to trigger the command.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets the command arguments which were used to trigger the command.
     * @return The command arguments which were used to trigger the command.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Gets the preffered language of user who invoked the command.
     * @return The preferred language of user.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Gets all passed arguments as concatenated string.
     * @return Combined command arguments.
     */
    public String getConcatArgs() {
        return concatArgs;
    }

    /**
     * Checks that the current command was invoked with arguments.
     * @return True if command was invoked with args.
     */
    public boolean hasArgs() {
        return !concatArgs.isEmpty();
    }

    /**
     * Gets all passed arguments starting from specified argument number as concatenated string.
     * @param count Count of arguments to be skipped
     * @return Combined command arguments starting from specified argument.
     */
    public String skipConcatArgs(int count) {
        return Arrays.stream(args).skip(count).collect(Collectors.joining(" "));
    }

    /**
     * Gets currently loaded bot configuration object.
     * @return The bot configuration.
     */
    public Settings getSettings() {
        return Settings.instance;
    }

    /**
     * Gets current i18n manager instance.
     * @return The i18n manager instance.
     */
    public I18n getI18n() {
        return i18n;
    }

    /**
     * Gets the original message object which was used to trigger the command.
     * @return The message object.
     */
    public Message getMessage() {
        return event.getMessage();
    }

    public void error(CharSequence message) {
        send(CommandIcons.ERROR + message, null);
    }

    public void error(CharSequence message, Consumer<Message> callback) {
        send(CommandIcons.ERROR + message, callback);
    }

    public void success(CharSequence message) {
        send(CommandIcons.SUCCESS + message, null);
    }

    public void success(CharSequence message, Consumer<Message> callback) {
        send(CommandIcons.SUCCESS + message, callback);
    }

    public void info(CharSequence message) {
        send(CommandIcons.INFO + message, null);
    }

    public void info(CharSequence message, Consumer<Message> callback) {
        send(CommandIcons.INFO + message, callback);
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
            event.getChannel().sendMessage(CommandIcons.ERROR + getTranslated("generic.sensitive")).queue(callback);
        } else {
            event.getChannel().sendMessage(message).queue(callback);
        }
    }

    public void send(MessageEmbed message, Consumer<Message> callback) {
        event.getChannel().sendMessage(message).queue(callback);
    }

    public String getTranslated(String key) {
        return i18n.get(language, key);
    }

    public String transFormat(String key, Object... args) {
        return String.format(getTranslated(key), args);
    }

    public EmbedBuilder getNormalEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(getNormalColor());
        return eb;
    }

    public Color getNormalColor() {
        Color c = Settings.instance.bot.normalColor;

        if (event.getMember() != null) {
            if (event.getMember().getColor() != null) {
                c = event.getMember().getColor();
            }
        }

        return c;
    }

    public EmbedBuilder getErrorEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Settings.instance.bot.errorColor);
        eb.setFooter(Settings.instance.bot.botName + " v" + Constants.VERSION + " | created by gabixdev & contributors", null);
        return eb;
    }

    public boolean checkSensitive(String input) {
        if (input.contains(Settings.instance.connection.token)) return true;
        return Settings.instance.apiKeys.values().stream().anyMatch(input::contains);
    }
}
