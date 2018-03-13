package me.gabixdev.kyoko.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class DatabaseManager {
    private final Kyoko kyoko;
    private Settings settings;
    private ConnectionSource connectionSource;
    private Dao<UserConfig, Integer> userDao;

    public DatabaseManager(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    public void load(Settings settings) {
        this.settings = settings;

        String databaseURL = "jdbc:mysql://" + settings.getMysqlHost() + ":" + settings.getMysqlPort() + "/" + settings.getMysqlDatabase() + "?useSSL=false&user=" + settings.getMysqlUser() + "&password=" + settings.getMysqlPassword();
        try {
            connectionSource = new JdbcConnectionSource(databaseURL);
            userDao = DaoManager.createDao(connectionSource, UserConfig.class);
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
            kyoko.getLog().info("Creating new user configuration for " + user.getName() + " (" + user.getId() + ")");
            uc = new UserConfig(user.getIdLong(), Language.ENGLISH, 0, 0, 0, 0L);
            userDao.create(uc);
        }
        return uc;
    }

    public void saveUser(User u, UserConfig uc) {
        try {
            userDao.createOrUpdate(uc);
            kyoko.getLog().info("User saved: " + u.getName() + " (" + u.getId() + ")");
        } catch (Exception e) {
            kyoko.getLog().severe("Error saving user " + u.getName() + " (" + u.getId() + ")!");
            e.printStackTrace();
        }
    }
}
