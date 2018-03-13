package me.gabixdev.kyoko.i18n;

import me.gabixdev.kyoko.Kyoko;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class I18n {
    private final Kyoko kyoko;
    private HashMap<Language, Properties> langs;

    public I18n(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.langs = new HashMap<>();
    }

    public void loadMessages() {
        for (Language l : Language.values()) {
            Properties p = new Properties();
            URL u = getClass().getResource("/messages_" + l.getShortName() + ".properties");
            if (u == null) {
                kyoko.getLog().warning("Messages file for language " + l.name() + " does not exists.");
                continue;
            }
            try {
                p.load(getClass().getResourceAsStream("/messages_" + l.getShortName() + ".properties"));
                langs.put(l, p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String get(Language l, String key) {
        if (langs.containsKey(l))
            return langs.get(l).getProperty(key, langs.get(Language.ENGLISH).getProperty(key, key));
        return langs.get(Language.ENGLISH).getProperty(key, key);
    }

    public Language getLanguage(Guild guild) {
        return Language.ENGLISH;
    }

    public Language getLanguage(Member member) {
        try {
            return kyoko.getDatabaseManager().getUser(member.getUser()).language;
        } catch (Exception e) {
            e.printStackTrace();
            return Language.ENGLISH;
        }
    }
}
