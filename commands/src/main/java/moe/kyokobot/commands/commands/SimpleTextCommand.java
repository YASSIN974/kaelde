package moe.kyokobot.commands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import org.jetbrains.annotations.NotNull;

public class SimpleTextCommand extends Command {

    private final String text;

    public SimpleTextCommand(String name, String text) {
        this.text = text;
        this.name = name;
        this.description = text;
        this.usage = "";
        this.category = CommandCategory.FUN;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        context.send(text);
    }
}
