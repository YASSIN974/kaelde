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
        if (context.getArgs().length == 0) {
            context.send(context.error() + "" + context.getTranslated("say.error"));
        } else {
            context.send(context.getConcatArgs());
        }
    }
}
