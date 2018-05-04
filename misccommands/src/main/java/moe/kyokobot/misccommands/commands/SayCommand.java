package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;

public class SayCommand extends Command {
	private CommandManager commandManager;

    public SayCommand(CommandManager commandManager) {
        this.commandManager = commandManager;


        name = "say";
        category = CommandCategory.UTILITY;
    }


    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            context.send(context.error() + "You have not written anything!");
        } else {
            context.send(context.getConcatArgs());
        }
    }
}
