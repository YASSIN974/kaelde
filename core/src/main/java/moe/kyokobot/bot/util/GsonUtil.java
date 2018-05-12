package moe.kyokobot.bot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
    public static Gson gson;

    static {
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public static String toJSON(Object object) {
        return gson.toJson(object);
    }
}
