package moe.kyokobot.bot.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import io.sentry.Sentry;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.event.GuildCountUpdateEvent;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GuildCountService extends AbstractScheduledService {
    private final Logger logger = LoggerFactory.getLogger(GuildCountService.class);
    private JDA jda;
    private ShardManager shardManager;

    private int guilds;
    private int last = 0;

    public GuildCountService(ShardManager shardManager) {
        this.shardManager = shardManager;

        guilds = shardManager.getGuilds().size();
    }

    public GuildCountService(JDA jda) {
        this.jda = jda;

        guilds = jda.getGuilds().size();
    }

    @Override
    protected void runOneIteration() throws Exception {
        Settings settings = Settings.instance;
        if (settings.bot.games.isEmpty()) return;
        if (last >= settings.bot.games.size()) last = 0;

        try {
            if (shardManager != null) {
                shardManager.getShards().forEach(jda -> {
                    jda.getPresence().setGame(Game.of(settings.bot.gameType,
                            settings.bot.games.get(last)
                                    .replace("{prefix}", settings.bot.normalPrefix)
                                    .replace("{shard}", String.valueOf(jda.getShardInfo().getShardId()))
                                    .replace("{guilds}", String.valueOf(jda.getGuilds().size()))
                                    .replace("{count}", String.valueOf(jda.getShardInfo().getShardTotal()))));
                });
                last++;
            } else {
                jda.getPresence().setGame(Game.of(settings.bot.gameType,
                        settings.bot.games.get(last)
                                .replace("{prefix}", settings.bot.normalPrefix)
                                .replace("{guilds}", String.valueOf(guilds))));
                last++;
            }
        } catch (Exception e) {
            logger.error("Caught error while updating guild stats!", e);
            Sentry.capture(e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 2, TimeUnit.MINUTES);
    }

    @Subscribe
    public void update(GuildCountUpdateEvent event) {
        if (event != null) {
            guilds = event.getGuildCount();
        }
    }
}
