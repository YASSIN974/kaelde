package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.RandomUtil;
import org.jetbrains.annotations.NotNull;

public class RandomTextCommand extends Command {

    private final String[] texts;

    public RandomTextCommand(String name, String[] texts) {
        if (texts.length == 0) throw new IllegalArgumentException("texts array should contain at least 1 element!");

        this.texts = texts;
        this.name = name;
        this.description = texts[0];
        this.usage = "";
        this.category = CommandCategory.FUN;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        context.send(RandomUtil.randomElement(texts));
    }
}
