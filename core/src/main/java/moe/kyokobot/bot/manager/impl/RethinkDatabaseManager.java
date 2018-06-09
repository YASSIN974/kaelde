package moe.kyokobot.bot.manager.impl;

import com.rethinkdb.net.Connection;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.entity.DatabaseEntity;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.GsonUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.rethinkdb.RethinkDB.r;

public class RethinkDatabaseManager implements DatabaseManager {
    private Logger logger;
    private Settings settings;
    private Connection connection;

    public RethinkDatabaseManager(Settings settings) {
        this.settings = settings;
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void load() {
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
    public UserConfig getUser(User user) throws Exception {
        String json = r.table("users").get(user.getId()).toJson().run(connection);
        return (json != null && !json.equals("null")) ? GsonUtil.fromJSON(json, UserConfig.class) : newUser(user.getId());
    }

    @Override
    public GuildConfig getGuild(Guild guild) throws Exception {
        String json = r.table("users").get(guild.getId()).toJson().run(connection);
        return (json != null && !json.equals("null")) ? GsonUtil.fromJSON(json, GuildConfig.class) : newGuild(guild.getId());
    }

    @Override
    public void save(@NotNull DatabaseEntity entity) {
        System.out.println(UserConfig.class.getConstructors()[0].getParameters()[0].getName());
        logger.debug("Saved entity on " + entity.getTableName() + ": " + entity.getClass().getName() + ": " + entity.toString());
        r.table(entity.getTableName()).insert(r.json(GsonUtil.toJSON(entity))).optArg("conflict", "replace").runNoReply(connection);
    }

    private UserConfig newUser(String id) {
        return new UserConfig( "default", 0L, 1L, 0L, 0L,0L, Language.DEFAULT, id, new ArrayList<>());
    }

    private GuildConfig newGuild(String id) {
        return new GuildConfig(id, Language.ENGLISH, new ArrayList<>());
    }

    private UserConfig fillUserDefaults(UserConfig config) {
        return config;
    }
}
