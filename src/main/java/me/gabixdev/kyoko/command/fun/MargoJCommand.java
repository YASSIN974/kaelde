package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class MargoJCommand extends Command {
    private final String[] aliases = new String[]{"margoj"};
    private Kyoko kyoko;

    public MargoJCommand(Kyoko kyoko) {
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
        return "margoj.description";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        message.getTextChannel().sendMessage("https://www.mpcforum.pl/topic/1622891-margoj-plany-otwartego-i-w-pe%C5%82ni-dostosowywalnego-serwera-margonem/").queue();
    }
}
