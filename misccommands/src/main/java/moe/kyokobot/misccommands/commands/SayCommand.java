package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.Permission;

public class SayCommand extends Command {
    private CommandManager commandManager;

    public SayCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        name = "say";
        category = CommandCategory.UTILITY;
        usage = "generic.textusage";
    }


    @Override
    public void execute(CommandContext context) {
        if (context.hasArgs()) {
            String msg = context.getConcatArgs();
            if (!context.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
                msg = msg.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere");
            }
            context.send(msg);
        } else {
            CommonErrors.usage(context);
        }
    }
}
