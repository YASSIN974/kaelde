package moe.kyokobot.bot.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import io.sentry.Sentry;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.event.GuildCountUpdateEvent;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.util.concurrent.TimeUnit;

public class GuildCountService extends AbstractScheduledService {
    private JDA jda;
    private ShardManager shardManager;

    private int guilds = 0;

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

        try {
            if (shardManager != null) {
                shardManager.getShards().forEach(jda -> {
                    jda.getPresence().setGame(Game.of(settings.bot.gameType,
                            settings.bot.game
                                    .replace("{prefix}", settings.bot.normalPrefix)
                                    .replace("{shard}", String.valueOf(jda.getShardInfo().getShardId()))
                                    .replace("{count}", String.valueOf(jda.getShardInfo().getShardTotal()))));
                });
            } else {
                jda.getPresence().setGame(Game.of(settings.bot.gameType,
                        settings.bot.game
                                .replace("{prefix}", settings.bot.normalPrefix)
                                .replace("{guilds}", String.valueOf(guilds))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.MINUTES);
    }

    @Subscribe
    public void update(GuildCountUpdateEvent event) {
        if (event != null) {
            guilds = event.getGuildCount();
        }
    }
}
