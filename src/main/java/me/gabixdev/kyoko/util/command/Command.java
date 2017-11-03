package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
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

    public void printUsage(Kyoko kyoko, Language lang, TextChannel c) {
        c.sendMessage(kyoko.getAbstractEmbedBuilder().getUsageBuilder(lang, getLabel(), kyoko.getI18n().get(lang, getUsage())).build()).queue();
    }

    public void handle(Message message, Event event, String[] args) throws Throwable {

    }
}