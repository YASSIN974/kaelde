package moe.kyokobot.bot.util;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
    public static Gson gson;

    static {
        gson = new GsonBuilder().disableHtmlEscaping().create();
        //JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static String toJSON(Object object) {
        //return JsonStream.serialize(gsonCompat, object);
        return gson.toJson(object);
    }

    public static <T> T fromJSON(byte[] data, Class<T> object) {
        /*try {
            return JsonIterator.parse(data).read(object);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
        //return JsonIterator.deserialize(gson, data, object);
        return gson.fromJson(new String(data, Charsets.UTF_8), object);
    }

    public static <T> T fromJSON(String text, Class<T> object) {
        //return JsonIterator.deserialize(gson, text, object);
        return gson.fromJson(text, object);
    }
}
