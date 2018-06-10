package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;

import java.time.temporal.ChronoUnit;

public class PingCommand extends Command {
	private CommandManager commandManager;

    public PingCommand(CommandManager commandManager) {
    	this.commandManager = commandManager;
    	
    	name = "ping";
    	category = CommandCategory.UTILITY;
    }
    
    @Override
    public void execute(CommandContext context) {
    	context.send("🏓  |  Ping: ...ms | Gateway: ...ms", message -> {
            long ping = context.getEvent().getMessage().getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS);
            message.editMessage("🏓  |  Ping: " + ping + " ms | Gateway: " + context.getMessage().getJDA().getPing() + "ms").queue();
        });
    }
}
