package moe.kyokobot.bot.i18n;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Charsets;
import com.google.common.eventbus.Subscribe;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.event.DatabaseUpdateEvent;
import moe.kyokobot.bot.manager.impl.RethinkDatabaseManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class I18n {
    private RethinkDatabaseManager databaseManager;
    private Logger logger;
    private HashMap<Language, Properties> languages;
    private Cache<String, Language> languageCache;

    public I18n(RethinkDatabaseManager databaseManager) {
        logger = LoggerFactory.getLogger(getClass());
        this.databaseManager = databaseManager;
        languageCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(1000).build();
    }

    public void loadMessages() {
        logger.debug("Loading messages...");
        languages = new HashMap<>();

        for (Language l : Language.values()) {
            if (l != Language.DEFAULT) try {
                Properties p = new Properties();
                File f = new File("./" + l.getShortName() + "/messages.properties");
                URL url;
                if (f.exists()) {
                    url = f.toURI().toURL();
                    logger.debug("Loaded language {} from filesystem: {}", l.getShortName(), url.toString());
                } else {
                    url = getClass().getResource("/" + l.getShortName() + "/messages.properties");
                    if (url == null) {
                        logger.warn("Messages file for language {} does not exists.", l.name());
                        continue;
                    }
                    logger.debug("Loaded language {} from jar: {}", l.getLocalized(), url.toString());
                }

                p.load(new InputStreamReader(url.openStream(), Charsets.UTF_8));
                languages.put(l, p);
            } catch (Exception e) {
                logger.error("Error while loading language!", e);
            }
        }
    }

    public String get(Language l, String key) {
        if (languages.containsKey(l))
            return languages.get(l).getProperty(key, languages.get(Language.ENGLISH).getProperty(key, key));
        return languages.get(Language.ENGLISH).getProperty(key, key);
    }

    public Language getLanguage(Guild guild) {
        try {
            Language l = languageCache.get(guild.getId(),
                    gid -> databaseManager.getGuild(guild).getLanguage());

            return l == Language.DEFAULT ? Language.ENGLISH : l;
        } catch (Exception e) {
            logger.error("Error while getting guild language!", e);
            return Language.ENGLISH;
        }
    }

    public Language getLanguage(Member member) {
        try {
            Language l = languageCache.get(member.getUser().getId(),
                    uid -> databaseManager.getUser(member.getUser()).getLanguage());

            return l == Language.DEFAULT ? getLanguage(member.getGuild()) : l;
        } catch (Exception e) {
            logger.error("Error while getting user language!", e);
            return Language.ENGLISH;
        }
    }

    public Language getLanguage(User user) {
        try {
            Language l = languageCache.get(user.getId(),
                    uid -> databaseManager.getUser(user).getLanguage());

            return l == Language.DEFAULT ? Language.ENGLISH : l;
        } catch (Exception e) {
            logger.error("Error while getting user language!", e);
            return Language.ENGLISH;
        }
    }

    @Subscribe
    public void onUpdate(DatabaseUpdateEvent event) {
        if (event.getEntity() instanceof UserConfig) {
            languageCache.invalidate(((UserConfig) event.getEntity()).getId());
        } else if (event.getEntity() instanceof GuildConfig) {
            languageCache.invalidate(((GuildConfig) event.getEntity()).getGuildId());
        }
    }
}
