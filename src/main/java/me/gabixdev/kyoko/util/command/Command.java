package me.gabixdev.kyoko.util.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public abstract class Command {
    public String getLabel() {
        return "";
    }

    public String[] getAliases() {
        return new String[] {};
    }

    public String getDescription() {
        return "";
    }

    public String getUsage() {
        return "";
    }

    public CommandType getType() {
        return CommandType.BASIC;
    }

    public void handle(Message message, Event event, String[] args) throws Throwable {

    }
}