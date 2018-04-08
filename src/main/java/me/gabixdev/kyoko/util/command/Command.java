package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;

public abstract class Command {
    protected String label;
    protected String[] aliases;
    protected String description;
    protected String usage;
    protected CommandCategory category;

    public String getLabel() {
        return label;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public void printUsage(Kyoko kyoko, Language lang, TextChannel c) {
        c.sendMessage(kyoko.getAbstractEmbedBuilder().getUsageBuilder(lang, getLabel(), kyoko.getI18n().get(lang, getUsage())).build()).queue();
    }

    public void printNSFW(Kyoko kyoko, Language lang, TextChannel c) {
        c.sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField(kyoko.getI18n().get(lang, "generic.error"), String.format(kyoko.getI18n().get(lang, "generic.nsfw"), kyoko.getSettings().getPrefix()), false).build()).queue();
    }

    public void handle(Message message, Event event, String[] args) throws Throwable {

    }
}