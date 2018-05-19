package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.RandomUtil;

import java.util.Arrays;
import java.util.Random;

public class RandomTextCommand extends Command {
    private static final Random random = new Random();
    private final String[] texts;

    public RandomTextCommand(String name, String[] texts) {
        if (texts.length == 0) throw new IllegalArgumentException("texts array should contain at least 1 element!");

        this.texts = texts;
        this.name = name;
        this.description = texts[0];
        this.category = CommandCategory.FUN;
    }

    @Override
    public void execute(CommandContext context) {
        context.send(RandomUtil.randomElement(texts));
    }
}
