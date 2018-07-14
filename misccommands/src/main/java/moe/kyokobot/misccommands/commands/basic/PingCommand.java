package moe.kyokobot.misccommands.commands.basic;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;

import java.time.temporal.ChronoUnit;

public class PingCommand extends Command {

    public PingCommand() {
    	name = "ping";
        usage = "";
    	category = CommandCategory.BASIC;
    }
    
    @Override
    public void execute(CommandContext context) {
    	context.send("🏓  |  Ping: ...ms | Gateway: ...ms", message -> {
            long ping = context.getEvent().getMessage().getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS);
            message.editMessage("🏓  |  Ping: " + ping + " ms | Gateway: " + context.getMessage().getJDA().getPing() + "ms").queue();
        });
    }
}
