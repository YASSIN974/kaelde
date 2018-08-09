package moe.kyokobot.commands.commands.dev;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.CommandType;
import org.jetbrains.annotations.NotNull;

public class SetNameCommand extends Command {
    public SetNameCommand() {
        name = "setname";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.hasArgs()) {
            context.getMessage().getJDA().getSelfUser().getManager().setName(context.getConcatArgs()).queue(
                    success -> context.send(CommandIcons.SUCCESS + "Name updated!"),
                    error -> {
                        error.printStackTrace();
                        Sentry.capture(error);
                        context.send(CommandIcons.ERROR + "Error while updating name! `" + error.getMessage() + "`");
                    });
        } else {
            context.send("usage: `" + context.getPrefix() + name + " [link or attachment]`");
        }
    }
}
