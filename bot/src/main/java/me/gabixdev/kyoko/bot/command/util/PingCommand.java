package me.gabixdev.kyoko.bot.command.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import net.dv8tion.jda.core.EmbedBuilder;

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
        EmbedBuilder eb = context.getNormalEmbed();
        eb.addField(context.getTranslated("ping.message"), "Ping: ... ms | Gateway: " + kyoko.getJda().getPing() + "ms", false);
        context.getChannel().sendMessage(eb.build()).queue(message -> {
            long ping = context.getEvent().getMessage().getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS);
            EmbedBuilder bb = context.getNormalEmbed();
            bb.addField(context.getTranslated("ping.message"), "Ping: " + ping + " ms | Gateway: " + kyoko.getJda().getPing() + "ms", false);
            message.editMessage(bb.build()).queue();
        });
    }
}
