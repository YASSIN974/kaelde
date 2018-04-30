package moe.kyokobot.bot.i18n;

import com.google.common.base.Charsets;
import moe.kyokobot.bot.manager.DatabaseManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

public class I18n {
    private DatabaseManager databaseManager;
    private Logger logger;
    private HashMap<Language, Properties> langs;
    private HashMap<Long, Language> languageCache;

    public I18n(DatabaseManager databaseManager) {
        logger = LoggerFactory.getLogger(getClass());
        this.databaseManager = databaseManager;
        languageCache = new HashMap<>();
    }

    public void loadMessages() {
        logger.debug("Loading messages...");
        langs = new HashMap<>();

        for (Language l : Language.values()) {
            try {
                Properties p = new Properties();
                File f = new File("./messages/messages_" + l.getShortName() + ".properties");
                URL url;
                if (f.exists()) {
                    url = f.toURI().toURL();
                    logger.debug("Loaded language " + l.getShortName() + " from filesystem: " + url.toString());
                } else {
                    url = getClass().getResource("/messages_" + l.getShortName() + ".properties");
                    if (url == null) {
                        logger.warn("Messages file for language " + l.name() + " does not exists.");
                        continue;
                    }
                    logger.debug("Loaded language " + l.getShortName() + " from jar: " + url.toString());
                }

                p.load(new InputStreamReader(url.openStream(), Charsets.UTF_8));
                langs.put(l, p);
            } catch (Exception e) {
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
        // TODO
        return Language.ENGLISH;
    }

    public Language getLanguage(Member member) {
        try { // TODO guild lang
            if (!languageCache.containsKey(member.getUser().getIdLong())) {
                logger.debug("Loading user language to cache...");
                Language l = databaseManager.getUser(member.getUser()).language;
                languageCache.put(member.getUser().getIdLong(), l);
            }
            return languageCache.get(member.getUser().getIdLong());
        } catch (Exception e) {
            e.printStackTrace();
            return Language.ENGLISH;
        }
    }

    public Language getLanguage(User user) {
        try {
            if (!languageCache.containsKey(user.getIdLong())) {
                logger.debug("Loading user language to cache...");
                Language l = databaseManager.getUser(user).language;
                languageCache.put(user.getIdLong(), l);
            }
            return languageCache.get(user.getIdLong());
        } catch (Exception e) {
            e.printStackTrace();
            return Language.ENGLISH;
        }
    }
}
