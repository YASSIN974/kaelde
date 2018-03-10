package me.gabixdev.kyoko.bot.command;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandContext {
    private final MessageReceivedEvent event;
    private final String label;
    private final String concatArgs;
    private final String[] args;

    public CommandContext(MessageReceivedEvent event, String label, String concatArgs, String[] args) {
        this.event = event;
        this.label = label;
        this.concatArgs = concatArgs;
        this.args = args;
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

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }
}
