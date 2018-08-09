package moe.kyokobot.commands.commands.fun;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.Permission;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.bot.util.RandomUtil.randomElement;

public class OwOifyCommand extends Command {
    private static final String[] faces = new String[] {"owo", "OWO", "OwO", "UwU", ">w<", "^w^", "uwu"};

    public OwOifyCommand() {
        name = "owoify";
        description = "owo.description";
        usage = "generic.textusage";
        aliases = new String[] {"owo"};
        category = CommandCategory.FUN;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.hasArgs()) {
            String text = context.getConcatArgs();
            if (!context.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
                text = text.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere");
            }

            text = text.replace("l", "w").replace("r", "w");
            text = text.replace("L", "W").replace("R", "W");
            text = text.replace("ove", "uv");
            text = text.replace("n", "ny");
            text = text.replace("N", "NY");
            text = text.replace("!", randomElement(faces));

            if (text.length() > 2000) {
                CommonErrors.tooLong(context);
                return;
            }

            context.send(text);
        } else {
            CommonErrors.usage(context);
        }
    }

    private class NekosResponse {
        private String owo;
    }
}
