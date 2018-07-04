package moe.kyokobot.social;

import com.google.common.eventbus.Subscribe;
import io.sentry.Sentry;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.RandomUtil;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelHandler {
    public static final int LEVEL_THRESHOLD = 1000;

    private final Logger logger = LoggerFactory.getLogger(LevelHandler.class);
    private final DatabaseManager databaseManager;

    public LevelHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Subscribe
    public void onMessage(MessageReceivedEvent event) {
        if (event.getMessageIdLong() % 2 == 0) {
            try {
                UserConfig data = databaseManager.getUser(event.getAuthor());
                long oldlevel = data.getLevel();
                int exp = 5 + RandomUtil.random.nextInt(10);

                data.setXp(data.getXp() + exp);
                long nextLevelXP = LEVEL_THRESHOLD * data.getLevel();
                while (data.getXp() >= nextLevelXP) {
                    data.setXp(data.getXp() - nextLevelXP);
                    data.setLevel(data.getLevel() + 1);
                    nextLevelXP = LEVEL_THRESHOLD * data.getLevel();
                }

                if (oldlevel != data.getLevel()) {
                    // TODO inform user on level advancement?
                    logger.debug("User {} advanced from level {} to level {}.", UserUtil.toDiscrim(event.getAuthor()), oldlevel, data.getLevel());
                }

                databaseManager.save(data);
            } catch (Exception e) {
                logger.error("Error querying user data!", e);
                Sentry.capture(e);
            }
        }
    }
}
