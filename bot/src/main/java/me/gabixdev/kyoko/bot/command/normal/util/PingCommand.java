package me.gabixdev.kyoko.bot.command.normal.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;

import java.time.temporal.ChronoUnit;

public class PingCommand extends Command {
    private final Kyoko kyoko;

    public PingCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "ping";
        this.category = CommandCategory.UTILITY;
        this.description = "ping.description";
        this.usage = null;
    }

    @Override
    public void execute(CommandContext context) {
        String clientId = kyoko.getJda().getSelfUser().getId();
        context.send("Ping: ... ms | Gateway: " + kyoko.getJda().getPing() + "ms", message -> {
            long ping = context.getEvent().getMessage().getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS);
            message.editMessage("Ping: " + ping + " ms | Gateway: " + kyoko.getJda().getPing() + "ms").queue();
        });
    }
}
