package moe.kyokobot.bot.command.debug;


import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.CommandType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CleanSelfCommand extends Command {
    public CleanSelfCommand() {
        name = "cleanself";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        AtomicInteger i = new AtomicInteger();

        context.getChannel().getHistory().retrievePast(100).queue(messages -> messages.forEach(message -> {
            if (message.getAuthor().getIdLong() == context.getEvent().getJDA().getSelfUser().getIdLong()) {
                i.incrementAndGet();
                message.delete().queue();
            }
        }));

        context.send(CommandIcons.SUCCESS + "Cleaned " + i.get() + " messages :ok_hand:", message -> message.delete().queueAfter(1, TimeUnit.SECONDS));
    }
}