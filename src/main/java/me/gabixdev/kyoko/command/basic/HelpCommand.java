package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class HelpCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"help"};

    public HelpCommand(Kyoko kyoko) {
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
        return "help.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getGuild());
        StringBuilder labels = new StringBuilder();
        StringBuilder descs = new StringBuilder();
        for (Command command : kyoko.getCommandManager().getCommands()) {
            // TODO: per-guild prefix and language
            labels.append(kyoko.getSettings().getPrefix()).append(command.getLabel()).append("\n");
            descs.append(kyoko.getI18n().get(l, command.getDescription())).append("\n");
        }
        String ls = labels.toString();
        String ds = descs.toString();
        normal.addField("_Command system is being rewritten, not ready to use_", "", false);
        normal.addField(kyoko.getI18n().get(l, "help.header.commands"), ls.substring(0, ls.length() - 1), true);
        normal.addField(kyoko.getI18n().get(l, "help.header.description"), ds.substring(0, ds.length() - 1), true);
        message.getChannel().sendMessage(normal.build()).queue();
    }
}
