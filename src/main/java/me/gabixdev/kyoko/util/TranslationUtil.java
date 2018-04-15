package me.gabixdev.kyoko.util;

import me.gabixdev.kyoko.CreateMissingTranslations;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.utils.tuple.Pair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class TranslationUtil {
    private static HashMap<Language, Integer> completeness;

    public static int getTranslationCompleteness(Language l) {
        if (l == Language.ENGLISH) return 100;

        if (completeness == null) completeness = new HashMap<>();
        if (completeness.containsKey(l)) return completeness.get(l);

        Properties english = new Properties();
        try {
            english.load(new InputStreamReader(CreateMissingTranslations.class.getResourceAsStream("/messages_en.properties"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        Properties p = new Properties();
        URL u = TranslationUtil.class.getResource("/messages_" + l.getShortName() + ".properties");
        if (u == null) {
            return 0;
        }
        try {
            p.load(new InputStreamReader(CreateMissingTranslations.class.getResourceAsStream("/messages_" + l.getShortName() + ".properties"), "UTF-8"));
            int max = p.keySet().size();
            int translated = 0;
            for (Object k : english.keySet()) {
                if (!p.getProperty((String) k, (String) k).equals(english.getProperty((String) k))) translated++;
            }

            int percent = (int) Math.floor(((float) translated / (float) max) * 100);

            if (percent > 85) percent = 100; // not all strings can be translated
            
            completeness.put(l, percent);
            return percent;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
