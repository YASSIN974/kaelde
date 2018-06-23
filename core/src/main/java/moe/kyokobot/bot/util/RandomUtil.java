package moe.kyokobot.bot.util;

import java.util.Random;

public class RandomUtil {
    public static Random random = new Random();

    @SafeVarargs
    public static <T> T randomElement(T... items) {
        return items[random.nextInt(items.length)];
    }
}
