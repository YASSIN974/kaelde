package moe.kyokobot.social;

import com.google.common.eventbus.Subscribe;
import io.sentry.Sentry;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.RandomUtil;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelHandler {
    public static final int LEVEL_THRESHOLD = 1000;

    private final Logger logger = LoggerFactory.getLogger(LevelHandler.class);
    private final DatabaseManager databaseManager;
    private final I18n i18n;

    public LevelHandler(DatabaseManager databaseManager, I18n i18n) {
        this.databaseManager = databaseManager;
        this.i18n = i18n;
    }

    @Subscribe
    public void onMessage(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;

        if (event.getMessageIdLong() % 10 == 0) {
            try {
                UserConfig data = databaseManager.getUser(event.getAuthor());
                GuildConfig guildConfig = databaseManager.getGuild(event.getGuild());
                long oldLevel = data.getLevel();
                int exp = 5 + RandomUtil.random.nextInt(10);

                data.setXp(data.getXp() + exp);
                long nextLevelXP = LEVEL_THRESHOLD * data.getLevel();
                while (data.getXp() >= nextLevelXP) {
                    data.setXp(data.getXp() - nextLevelXP);
                    data.setLevel(data.getLevel() + 1);
                    nextLevelXP = LEVEL_THRESHOLD * data.getLevel();
                }

                if (oldLevel != data.getLevel()) {
                    if (guildConfig.getModerationConfig().isLevelupMessages()) {
                        Language l = i18n.getLanguage(event.getGuild());

                        event.getChannel().sendMessage(CommandIcons.LEVELUP +
                                String.format(i18n.get(l, "social.levelup"), event.getMember().getEffectiveName(), data.getLevel())).queue();
                    }
                    logger.debug("User {} advanced from level {} to level {}.", UserUtil.toDiscrim(event.getAuthor()), oldLevel, data.getLevel());
                }

                databaseManager.save(data);
            } catch (Exception e) {
                logger.error("Error querying user data!", e);
                Sentry.capture(e);
            }
        }
    }
}
