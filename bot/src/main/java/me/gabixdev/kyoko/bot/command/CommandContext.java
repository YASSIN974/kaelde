package me.gabixdev.kyoko.bot.command;

import me.gabixdev.kyoko.bot.Constants;
import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.i18n.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.function.Consumer;

public class CommandContext {
    private final Kyoko kyoko;
    private final Command command;
    private final Language language;
    private final MessageReceivedEvent event;
    private final String prefix;
    private final String label;
    private final String concatArgs;
    private final String[] args;

    public CommandContext(Kyoko kyoko, Command command, MessageReceivedEvent event, String prefix, String label, String concatArgs, String[] args) {
        this.kyoko = kyoko;
        this.command = command;
        this.prefix = prefix;
        if (event.getChannelType().isGuild()) this.language = kyoko.getI18n().getLanguage(event.getMember());
        else this.language = kyoko.getI18n().getLanguage(event.getAuthor());
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

    public void send(CharSequence message) {
        send(message, null);
    }

    public void send(MessageEmbed message) {
        send(message, null);
    }

    public void send(CharSequence message, Consumer<Message> callback) {
        event.getChannel().sendMessage(message).queue(callback);
    }

    public void send(MessageEmbed message, Consumer<Message> callback) {
        event.getChannel().sendMessage(message).queue();
    }

    public String getTranslated(String key) {
        return kyoko.getI18n().get(language, key);
    }

    public EmbedBuilder getNormalEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(getNormalColor());
        eb.setFooter(kyoko.getSettings().botBrand + " v" + Constants.VERSION + " | created by gabixdev, Oksi & contributors", null);
        return eb;
    }

    public Color getNormalColor() {
        Color c = kyoko.getSettings().normalColor;

        if (event.getMember() != null) {
            if (event.getMember().getColor() != null) {
                c = event.getMember().getColor();
            }
        }

        return c;
    }

    public EmbedBuilder getErrorEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(kyoko.getSettings().errorColor);
        eb.setFooter(kyoko.getSettings().botBrand + " v" + Constants.VERSION + " | created by gabixdev, Oksi & contributors", null);
        return eb;
    }
}
