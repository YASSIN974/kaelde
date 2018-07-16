package moe.kyokobot.bot.command.debug;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.CommandType;
import org.jetbrains.annotations.NotNull;

public class ReloadMessagesCommand extends Command {
    public ReloadMessagesCommand() {
        name = "reloadmessages";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        context.send(CommandIcons.WORKING + "Reloading messages...", message -> {
            context.getI18n().loadMessages();
            message.editMessage(CommandIcons.SUCCESS + "Messages reloaded!").queue();
        });
    }
}
