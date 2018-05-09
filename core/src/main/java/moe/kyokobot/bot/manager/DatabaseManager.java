package moe.kyokobot.bot.manager;

import com.rethinkdb.RethinkDB;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    public static final RethinkDB r = RethinkDB.r;

    private Logger logger;
    private Settings settings;

    public DatabaseManager(Settings settings) {
        this.settings = settings;
        logger = LoggerFactory.getLogger(getClass());
    }

    public void load() {

    }

    public UserConfig getUser(User user) throws Exception {
        return null;
    }

    public void saveUser(User u, UserConfig uc) {

    }

    public GuildConfig getGuild(Guild guild) throws Exception {
        return null;
    }

    public void saveGuild(Guild g, GuildConfig gc) {

    }
}
