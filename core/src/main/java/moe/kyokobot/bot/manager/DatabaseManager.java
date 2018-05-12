package moe.kyokobot.bot.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rethinkdb.net.Connection;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.entity.DatabaseEntity;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.rethinkdb.RethinkDB.r;

public class DatabaseManager {
    private Logger logger;
    private Settings settings;
    private Connection connection;

    public DatabaseManager(Settings settings) {
        this.settings = settings;
        logger = LoggerFactory.getLogger(getClass());
    }

    public void load() {
        connection = r.connection()
                .hostname(settings.connection.rethinkHost)
                .port(settings.connection.rethinkPort)
                .user(settings.connection.rethinkUser, settings.connection.rethinkPassword)
                .db(settings.connection.rethinkDbName)
                .connect();

        r.tableCreate("users").runNoReply(connection);
        r.tableCreate("guilds").runNoReply(connection);
    }

    public UserConfig getUser(User user) throws Exception {
        String json = r.table("users").get(user.getIdLong()).toJson().run(connection);
        return (json != null && !json.equals("null")) ? GsonUtil.gson.fromJson(json, UserConfig.class) : newUser(user.getIdLong());
    }

    public GuildConfig getGuild(Guild guild) throws Exception {
        String json = r.table("users").get(guild.getIdLong()).toJson().run(connection);
        return (json != null && !json.equals("null")) ? GsonUtil.gson.fromJson(json, GuildConfig.class)  : newGuild(guild.getIdLong());
    }

    public void save(@NotNull DatabaseEntity entity) {
        System.out.println(UserConfig.class.getConstructors()[0].getParameters()[0].getName());
        logger.debug("Saved entity on " + entity.getTableName() + ": " + entity.getClass().getName() + ": " + entity.toString());
        r.table(entity.getTableName()).insert(r.json(GsonUtil.toJSON(entity))).optArg("conflict", "replace").runNoReply(connection);
    }

    private UserConfig newUser(long id) {
        return new UserConfig( "default", 0L, 1L, 0L, 0L,0L, Language.DEFAULT, id);
    }

    private GuildConfig newGuild(long id) {
        return new GuildConfig(id, Language.ENGLISH, new ArrayList<>());
    }
}
