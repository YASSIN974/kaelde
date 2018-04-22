package moe.kyokobot.bot.manager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DatabaseManager {
    private Logger logger;
    private Settings settings;
    private ConnectionSource connectionSource;
    private Dao<UserConfig, Integer> userDao;
    private Dao<GuildConfig, Integer> guildDao;

    public DatabaseManager() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public void load(Settings settings) {
        this.settings = settings;

        String databaseURL = settings.connection.databaseUrl;
        try {
            connectionSource = new JdbcPooledConnectionSource(databaseURL);
            userDao = DaoManager.createDao(connectionSource, UserConfig.class);
            guildDao = DaoManager.createDao(connectionSource, GuildConfig.class);
            TableUtils.createTableIfNotExists(connectionSource, UserConfig.class);
            TableUtils.createTableIfNotExists(connectionSource, GuildConfig.class);
        } catch (SQLException e) {
            logger.info("Error connecting to database!");
            e.printStackTrace();
            throw new RuntimeException("Cannot connect to database!");
        }
    }

    public UserConfig getUser(User user) throws Exception {
        QueryBuilder<UserConfig, Integer> statementBuilder = userDao.queryBuilder();
        statementBuilder.where().like("userid", user.getIdLong());

        UserConfig uc = userDao.queryForFirst(statementBuilder.prepare());
        if (uc == null) {
            uc = new UserConfig(user.getIdLong(), null, 0, 0, 0, 0L);
            userDao.create(uc);
        }
        return uc;
    }

    public void saveUser(User u, UserConfig uc) {
        try {
            userDao.createOrUpdate(uc);
        } catch (Exception e) {
            logger.error("Error saving user " + u.getName() + " (" + u.getId() + ")!");
            e.printStackTrace();
        }
    }

    public GuildConfig getGuild(Guild guild) throws Exception {
        QueryBuilder<GuildConfig, Integer> statementBuilder = guildDao.queryBuilder();
        statementBuilder.where().like("guildid", guild.getIdLong());

        GuildConfig gc = guildDao.queryForFirst(statementBuilder.prepare());
        if (gc == null) {
            gc = new GuildConfig(guild.getIdLong(), null, "[]");
            guildDao.create(gc);
        }
        return gc;
    }

    public void saveGuild(Guild g, GuildConfig gc) {
        try {
            guildDao.createOrUpdate(gc);
        } catch (Exception e) {
            logger.error("Error saving guild " + g.getName() + " (" + g.getId() + ")!");
            e.printStackTrace();
        }
    }

    public Dao<UserConfig, Integer> getUserDao() {
        return userDao;
    }
}
