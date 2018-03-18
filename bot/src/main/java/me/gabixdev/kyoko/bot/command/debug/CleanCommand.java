package me.gabixdev.kyoko.bot.command.debug;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.command.CommandType;

public class CleanCommand extends Command {
    private final Kyoko kyoko;

    public CleanCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "clean";
        this.category = CommandCategory.UTILITY;
        this.type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        context.getChannel().getHistory().retrievePast(100).queue(messages -> {
            messages.forEach(message -> {
                if (message.getAuthor().getIdLong() == kyoko.getJda().getSelfUser().getIdLong()) {
                    message.delete().queue();
                }
            });
        });
        context.send(":ok_hand:", message -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                //
            }
            message.delete().queue();
        });
    }
}
