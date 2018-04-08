package me.gabixdev.kyoko.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class DatabaseManager {
    private final Kyoko kyoko;
    private Settings settings;
    private ConnectionSource connectionSource;
    private Dao<UserConfig, Integer> userDao;
    private Dao<GuildConfig, Integer> guildDao;

    public DatabaseManager(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    public void load(Settings settings) {
        this.settings = settings;

        String databaseURL = "jdbc:mysql://" + settings.getMysqlHost() + ":" + settings.getMysqlPort() + "/" + settings.getMysqlDatabase() + "?useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC" + "&user=" + settings.getMysqlUser() + "&password=" + settings.getMysqlPassword();
        try {
            connectionSource = new JdbcPooledConnectionSource(databaseURL);
            userDao = DaoManager.createDao(connectionSource, UserConfig.class);
            guildDao = DaoManager.createDao(connectionSource, GuildConfig.class);
        } catch (SQLException e) {
            kyoko.getLog().info("Error connecting to MySQL!");
            kyoko.setRunning(false);
            e.printStackTrace();
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
            kyoko.getLog().severe("Error saving user " + u.getName() + " (" + u.getId() + ")!");
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
            kyoko.getLog().severe("Error saving guild " + g.getName() + " (" + g.getId() + ")!");
            e.printStackTrace();
        }
    }

    public Dao<UserConfig, Integer> getUserDao() {
        return userDao;
    }
}
