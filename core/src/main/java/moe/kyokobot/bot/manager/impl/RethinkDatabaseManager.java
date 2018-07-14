package moe.kyokobot.bot.manager.impl;

import com.google.common.eventbus.EventBus;
import com.google.gson.JsonArray;
import com.rethinkdb.net.Connection;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.entity.DatabaseEntity;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.event.DatabaseUpdateEvent;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.GsonUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.rethinkdb.RethinkDB.r;

public class RethinkDatabaseManager implements DatabaseManager {
    private final EventBus eventBus;
    private final Logger logger;
    private Connection connection;

    public RethinkDatabaseManager(EventBus eventBus) {
        this.eventBus = eventBus;
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void load() {
        Settings settings = Settings.instance;

        connection = r.connection()
                .hostname(settings.connection.rethinkHost)
                .port(settings.connection.rethinkPort)
                .user(settings.connection.rethinkUser, settings.connection.rethinkPassword)
                .db(settings.connection.rethinkDbName)
                .connect();

        r.tableCreate("users").runNoReply(connection);
        r.tableCreate("guilds").runNoReply(connection);
        r.tableCreate("botsettings").runNoReply(connection);
    }

    @Override
    public UserConfig getUser(User user) {
        String json = r.table("users").get(user.getId()).toJson().run(connection);
        return (json != null && !json.equals("null")) ? GsonUtil.fromJSON(json, UserConfig.class) : newUser(user.getId());
    }

    @Override
    public GuildConfig getGuild(Guild guild) {
        String json = r.table("guilds").get(guild.getId()).toJson().run(connection);
        return (json != null && !json.equals("null")) ? GsonUtil.fromJSON(json, GuildConfig.class) : newGuild(guild.getId());
    }

    @Override
    public Map<String, Integer> getTopBalances() {
        JsonArray a = GsonUtil.fromJSON(r.table("users").orderBy(r.desc("money")).limit(10).run(connection).toString(), JsonArray.class);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        a.forEach(element -> {
            String id = element.getAsJsonObject().get("id").getAsString();
            int amount = element.getAsJsonObject().get("money").getAsInt();
            if (id == null || amount == 0) return;
            map.put(id, amount);
        });

        return map;
    }

    @Override
    public String getValue(User user, String key, String def) {
        UserConfig u = getUser(user);
        String keyR = u.getKvStore().get(key);
        return keyR == null ? def : keyR;
    }

    @Override
    public String getValue(User user, String key) {
        return getValue(user, key, null);
    }

    @Override
    public List<String> getList(User user, String key, List<String> def) {
        UserConfig u = getUser(user);
        ArrayList<String> keyR = u.getListStore().get(key);
        return keyR == null ? def : keyR;
    }

    @Override
    public List<String> getList(User user, String key) {
        return getList(user, key, null);
    }

    @Override
    public void save(@NotNull DatabaseEntity entity) {
        logger.debug("Saved entity on {} -> {} -> {}", entity.getTableName(), entity.getClass().getName(), entity.toString());
        r.table(entity.getTableName()).insert(r.json(GsonUtil.toJSON(entity))).optArg("conflict", "update").runNoReply(connection);
        eventBus.post(new DatabaseUpdateEvent(entity));
    }

    private UserConfig newUser(String id) {
        return new UserConfig( "default", 0L, 1L, 0L, 0L,0L, Language.DEFAULT, id, new HashMap<>(), new HashMap<>(), 1, false);
    }

    private GuildConfig newGuild(String id) {
        return new GuildConfig(id, Language.ENGLISH, new ArrayList<>());
    }

    private UserConfig fillUserDefaults(UserConfig config) {
        return config;
    }
}
