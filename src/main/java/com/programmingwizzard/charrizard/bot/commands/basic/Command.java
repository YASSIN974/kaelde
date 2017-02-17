package com.programmingwizzard.charrizard.bot.commands.basic;

import com.programmingwizzard.charrizard.bot.basic.CMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.awt.*;

/*
 * @author ProgrammingWizzard
 * @date 04.02.2017
 */
public abstract class Command {
    private final String prefix;

    public Command(String label) {
        this.prefix = label;
    }

    public abstract void handle(CMessage message, String[] args) throws RateLimitedException;

    public final void sendEmbedMessage(CMessage message, EmbedBuilder builder) {
        message.getChannel().sendMessage(getMessageBuilder().setEmbed(builder.build()).build()).queue();
    }

    public final void sendUsage(CMessage message, String usage) {
        if (usage == null || usage.isEmpty()) {
            return;
        }
        EmbedBuilder builder = getEmbedBuilder()
            .setColor(new Color(231, 76, 60))
            .addField("Correct usage", usage, true);
        message.getChannel().sendMessage(getMessageBuilder().setEmbed(builder.build()).build()).queue();
    }

    public final void sendError(CMessage message, String error) {
        if (error == null || error.isEmpty()) {
            return;
        }
        EmbedBuilder builder = getEmbedBuilder()
            .setColor(new Color(231, 76, 60))
            .addField("Error", error, true);
        message.getChannel().sendMessage(getMessageBuilder().setEmbed(builder.build()).build()).queue();
    }

    public final String getPrefix() {
        return prefix;
    }

    public final EmbedBuilder getEmbedBuilder() {
        return new EmbedBuilder()
            .setTitle("Charrizard")
            .setFooter("© 2017 Charrizard contributors", null)
                       .setUrl("https://github.com/CharrizardBot/Charrizard/")
            .setColor(new Color(46, 204, 113));
    }

    public final MessageBuilder getMessageBuilder() {
        return new MessageBuilder();
    }
}
