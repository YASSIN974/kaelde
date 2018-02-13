package me.gabixdev.kyoko.database;

import com.dieselpoint.norm.Database;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;

public class DatabaseManager {
    private final Kyoko kyoko;
    private Settings settings;
    private Database db;

    private HashMap<Guild, GuildConfig> guildCache;
    private HashMap<User, UserConfig> userCache;

    public DatabaseManager(Kyoko kyoko) {
        this.kyoko = kyoko;
        userCache = new HashMap<>();
    }

    public void load(Settings settings) {
        this.settings = settings;

        System.setProperty("norm.jdbcUrl", "jdbc:mysql://" + settings.getMysqlHost() + ":" + settings.getMysqlPort() + "/" + settings.getMysqlDatabase() + "?useSSL=false");
        System.setProperty("norm.user", settings.getMysqlUser());
        System.setProperty("norm.password", settings.getMysqlPassword());
        db = new Database();
    }

    public void cleanCache() {
        userCache.clear();
        guildCache.clear();
    }

    public UserConfig getUser(User user) throws Exception {
        if (userCache.containsKey(user)) return userCache.get(user);
        UserConfig uc = db.table("users").where("userid=?", user.getIdLong()).first(UserConfig.class);
        if (uc == null) {
            kyoko.getLog().info("Creating new user configuration for " + user.getName() + " (" + user.getId() + ")");
            uc = new UserConfig(user.getIdLong(), Language.ENGLISH.name(), 0, 0, 0);
            db.table("users").insert(uc);
            return uc;
        } else {
            userCache.put(user, uc);
            return uc;
        }
    }
}
