package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class PingCommand extends Command {
    private final String[] aliases = new String[]{"ping"};
    private Kyoko kyoko;

    public PingCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "ping.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILITY;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        normal.addField(kyoko.getI18n().get(l, "ping.message"), kyoko.getJda().getPing() + " ms", true);
        message.getTextChannel().sendMessage(normal.build()).queue();
    }
}
