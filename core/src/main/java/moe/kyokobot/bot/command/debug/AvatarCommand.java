package moe.kyokobot.bot.command.debug;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.NetworkUtil;
import net.dv8tion.jda.core.entities.Icon;

import java.io.IOException;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        name = "updateavatar";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.hasArgs()) {
            updateAvatar(context, context.getConcatArgs());
        } else if (!context.getMessage().getAttachments().isEmpty()) {
            updateAvatar(context, context.getMessage().getAttachments().get(0).getUrl());
        } else {
            context.send("usage: `" + context.getPrefix() + name + " <new name>`");
        }
    }

    private void updateAvatar(CommandContext context, String url) {
        try {
            byte[] data = NetworkUtil.download(url);
            context.getMessage().getJDA().getSelfUser().getManager().setAvatar(Icon.from(data)).queue(
                    success -> context.send(context.success() + "Avatar updated!"),
                    error -> {
                        error.printStackTrace();
                        Sentry.capture(error);
                        context.send(context.error() + "Error while updating avatar! `" + error.getMessage() + "`");
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Sentry.capture(e);
            CommonErrors.exception(context, e);
        }
    }
}
