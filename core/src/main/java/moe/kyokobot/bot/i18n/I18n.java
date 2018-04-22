package moe.kyokobot.bot.i18n;

import com.google.common.base.Charsets;
import moe.kyokobot.bot.manager.DatabaseManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class I18n {
    private DatabaseManager databaseManager;
    private Logger logger;
    private HashMap<Language, Properties> langs;

    public I18n(DatabaseManager databaseManager) {
        logger = LoggerFactory.getLogger(getClass());
        this.databaseManager = databaseManager;

        langs = new HashMap<>();
    }

    public void loadMessages() {
        for (Language l : Language.values()) {
            Properties p = new Properties();
            URL u = getClass().getResource("/messages_" + l.getShortName() + ".properties");
            if (u == null) {
                logger.warn("Messages file for language " + l.name() + " does not exists.");
                continue;
            }
            try {
                p.load(new InputStreamReader(getClass().getResourceAsStream("/messages_" + l.getShortName() + ".properties"), Charsets.UTF_8));
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
            return databaseManager.getUser(member.getUser()).language;
        } catch (Exception e) {
            e.printStackTrace();
            return Language.ENGLISH;
        }
    }

    public Language getLanguage(User user) {
        try {
            return databaseManager.getUser(user).language;
        } catch (Exception e) {
            e.printStackTrace();
            return Language.ENGLISH;
        }
    }
}
