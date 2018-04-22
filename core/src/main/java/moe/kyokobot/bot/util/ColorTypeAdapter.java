package moe.kyokobot.bot.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;

public class ColorTypeAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter i, final Color c) throws IOException {
        i.beginObject();
        i.name("red").value(c.getRed());
        i.name("green").value(c.getGreen());
        i.name("blue").value(c.getBlue());
        i.name("alpha").value(c.getAlpha());
        i.endObject();
    }

    @Override
    public Color read(JsonReader i) throws IOException {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;

        i.beginObject();
        while (i.hasNext()) {
            switch (i.nextName()) {
                case "red":
                    red = i.nextInt();
                    break;
                case "green":
                    green = i.nextInt();
                    break;
                case "blue":
                    blue = i.nextInt();
                    break;
                case "alpha":
                    alpha = i.nextInt();
                    break;
            }
        }
        i.endObject();

        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;
        if (alpha > 255) alpha = 255;

        Color c = new Color(red, green, blue, alpha);

        return c;
    }
}
