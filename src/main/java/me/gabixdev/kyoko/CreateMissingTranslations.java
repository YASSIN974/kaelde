package me.gabixdev.kyoko;

import me.gabixdev.kyoko.i18n.Language;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class CreateMissingTranslations {
    public static void main(String... args) {
        HashMap<Language, Properties> langs;

        int id = (int) System.currentTimeMillis() % 0xffffff;

        Properties english = new Properties();
        URL enurl = CreateMissingTranslations.class.getResource("/messages_en.properties");
        try {
            english.load(new InputStreamReader(CreateMissingTranslations.class.getResourceAsStream("/messages_en.properties"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Language l : Language.values()) {
            if (l == Language.ENGLISH) continue;

            Properties p = new Properties();
            URL u = CreateMissingTranslations.class.getResource("/messages_" + l.getShortName() + ".properties");
            if (u == null) {
                continue;
            }
            try {
                p.load(new InputStreamReader(CreateMissingTranslations.class.getResourceAsStream("/messages_" + l.getShortName() + ".properties"), "UTF-8"));
                PrintWriter writer = new PrintWriter("kyoko-i18n-" + l.getShortName() + "-" + id + ".txt", "UTF-8");
                writer.println("# Kyoko lang file: " + l.getLocalized());
                english.keySet().forEach(key -> {
                    String f = escape(p.getProperty((String) key, english.getProperty((String) key, (String) key)));
                    writer.println((String) key + "=" + f);
                });
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String escape(String in) {
        return in.replace("\n", "\\n");
    }
}
