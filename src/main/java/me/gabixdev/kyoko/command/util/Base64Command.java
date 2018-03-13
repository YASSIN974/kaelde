package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import org.apache.commons.codec.binary.Base64;

public class Base64Command extends Command {
    private final String[] aliases = new String[]{"base64"};
    private Kyoko kyoko;

    public Base64Command(Kyoko kyoko) {
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
        return "base64.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILITY;
    }

    @Override
    public String getUsage() {
        return "base64.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        String[] mp = new String[args.length - 1];
        System.arraycopy(args, 1, mp, 0, args.length - 1);
        String data = String.join(" ", mp);

        String based = Base64.encodeBase64String(data.getBytes("UTF-8"));
        if (based.length() > 2000) {
            Language l = kyoko.getI18n().getLanguage(message.getMember());
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "generic.toolong"), false);
            message.getChannel().sendMessage(err.build()).queue();
        } else {
            message.getTextChannel().sendMessage(based).queue();
        }
    }
}
