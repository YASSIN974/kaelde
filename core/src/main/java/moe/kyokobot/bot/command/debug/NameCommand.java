package moe.kyokobot.bot.command.debug;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;

public class NameCommand extends Command {
    public NameCommand() {
        name = "setname";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.hasArgs()) {
            context.getMessage().getJDA().getSelfUser().getManager().setName(context.getConcatArgs()).queue(
                    success -> context.send(context.success() + "Name updated!"),
                    error -> {
                        error.printStackTrace();
                        Sentry.capture(error);
                        context.send(context.error() + "Error while updating name! `" + error.getMessage() + "`");
                    });
        } else {
            context.send("usage: `" + context.getPrefix() + name + " [link or attachment]`");
        }
    }
}
