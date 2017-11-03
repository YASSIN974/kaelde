package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.Kyoko;
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

    public void handle(Message message, Event event, String[] args) throws Throwable {

    }
}