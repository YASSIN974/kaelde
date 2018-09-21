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

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class I18n {
    private RethinkDatabaseManager databaseManager;
    private Logger logger;
    private HashMap<Language, Map<String, String>> languages;
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
                File f = new File("./messages/" + l.getShortName() + "/messages.properties");

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

                loadMessages(l, url.openStream());
            } catch (Exception e) {
                logger.error("Error while loading language!", e);
            }
        }
    }

    /**
     * Loads messages for given language message map from InputStream.
     * @param language The language we want to load messages for.
     * @param inputStream The input stream which contains message file.
     * @throws IOException
     */
    public void loadMessages(Language language, InputStream inputStream) throws IOException {
        Properties p = new Properties();
        p.load(new InputStreamReader(inputStream, Charsets.UTF_8));
        loadMessages(language, p);
    }

    /**
     * Loads messages for given language message map from Properties object.
     * @param language The language we want to load messages for.
     * @param properties Properties instance with messages.
     */
    public void loadMessages(Language language, Properties properties) {
        Map messageMap = languages.computeIfAbsent(language, __ -> new HashMap<>());
        properties.forEach(messageMap::put);
    }

    /**
     * Gets message map of specified language.
     * @param language The language we want to get message map of.
     * @return Immutable message map of specified language.
     */
    public Map<String, String> getMessageMap(@Nonnull Language language) {
        return Collections.unmodifiableMap(languages.get(language));
    }

    /**
     * Gets message for specific language via specified key.
     * @param language Preferred language of message.
     * @param key Message key.
     * @return Translated message, English fallback or specified key if missing.
     */
    public String get(@Nonnull Language language, @Nonnull String key) {
        if (languages.containsKey(language))
            return languages.get(language).getOrDefault(key, languages.get(Language.ENGLISH).getOrDefault(key, key));
        return languages.get(Language.ENGLISH).getOrDefault(key, key);
    }

    /**
     * Gets language of specified guild.
     * @param guild The guild which we want to get language of.
     * @return The language of specified guild.
     */
    public Language getLanguage(@Nonnull Guild guild) {
        try {
            Language l = languageCache.get(guild.getId(),
                    gid -> databaseManager.getGuild(guild).getLanguage());

            return l == Language.DEFAULT ? Language.ENGLISH : l;
        } catch (Exception e) {
            logger.error("Error while getting guild language!", e);
            return Language.ENGLISH;
        }
    }

    /**
     * Gets language of specified member.
     * @param member The member which we want to get language of.
     * @return The language of specified member.
     */
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

    /**
     * Gets language of specified user.
     * @param user The user which we want to get language of.
     * @return The language of specified user.
     */
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
