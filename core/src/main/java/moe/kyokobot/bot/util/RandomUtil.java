package moe.kyokobot.bot.util;

import java.util.Random;

public class RandomUtil {
    private static Random random = new Random();

    public static <T> T randomElement(T... items) {
        return items[random.nextInt(items.length)];
    }
}
