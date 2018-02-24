package me.gabixdev.kyoko.command.fun;

import com.github.lalyos.jfiglet.FigletFont;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.ArrayList;
import java.util.List;

/*
 * @author Marvin W. (NurMarvin)
 * @date 24.02.2018
 */

public class KysCommand extends Command {

    private final String[] aliases = new String[]{"s", "kys", "killyourself"};
    private Kyoko kyoko;

    public KysCommand(Kyoko kyoko) {
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
        return "kys.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        message.getChannel().sendMessage(String.format(kyoko.getI18n().get(l, "kys.message"), new Object[] { message.getMember() })).queue();
    }
}
