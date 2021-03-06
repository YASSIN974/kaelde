package moe.kyokobot.bot.services;

import com.google.common.util.concurrent.AbstractScheduledService;
import io.sentry.Sentry;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.Settings;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GuildCountService extends AbstractScheduledService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final Logger logger = LoggerFactory.getLogger(GuildCountService.class);
    private ShardManager shardManager;

    private OkHttpClient client = new OkHttpClient();
    private int last = 0;

    public GuildCountService(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    protected void runOneIteration() {
        Settings settings = Settings.instance;

        setPresence(settings);
        sendStats(settings);
    }

    private void setPresence(Settings settings) {
        if (settings.bot.games.isEmpty()) return;
        if (last >= settings.bot.games.size()) last = 0;
        shardManager.getShards().forEach(jda ->
            jda.getPresence().setGame(Game.of(settings.bot.gameType,
                    settings.bot.games.get(last)
                            .replace("{prefix}", settings.bot.normalPrefix)
                            .replace("{shard}", String.valueOf(jda.getShardInfo().getShardId()))
                            .replace("{guilds}", String.valueOf(jda.getGuilds().size()))
                            .replace("{count}", String.valueOf(jda.getShardInfo().getShardTotal())))));
        last++;
    }

    private void sendStats(Settings settings) {
        sendDBL(settings);
        sendListcord(settings);
        sendDBots(settings);
    }

    private void sendDBL(Settings settings) {
        if (settings.apiKeys.containsKey("dbl")) {
            shardManager.getShards().forEach(shard -> {
                RequestBody body = RequestBody.create(JSON,
                        "{\"server_count\":" + shard.getGuilds().size() + ", \"shard_id\":"
                                + shard.getShardInfo().getShardId() + ", \"shard_count\":"
                                + shard.getShardInfo().getShardTotal() + "}");
                Request request = new Request.Builder()
                        .header("Authorization", settings.apiKeys.get("dbl"))
                        .url("https://discordbots.org/api/bots/" + shard.getSelfUser().getId() + "/stats")
                        .post(body)
                        .build();
                try {
                    client.newCall(request).execute().close();
                    Globals.noVoteLock = false;
                } catch (IOException e) {
                    logger.error("Error while sending stats to DBL!", e);
                    Globals.noVoteLock = true; // disable votelocks when DBL is down
                    Sentry.capture(e);
                }
            });
        }
    }

    private void sendListcord(Settings settings) {
        if (settings.apiKeys.containsKey("listcord")) {
            shardManager.getShards().forEach(shard -> {
                RequestBody body = RequestBody.create(JSON,
                        "{\"guilds\":" + shard.getGuilds().size() + ", \"shard\":"+ shard.getShardInfo().getShardId() + "}");
                Request request = new Request.Builder()
                        .header("Authorization", settings.apiKeys.get("listcord"))
                        .url("https://listcord.com/api/bot/" + shard.getSelfUser().getId() + "/guilds")
                        .post(body)
                        .build();
                try {
                    client.newCall(request).execute().close();
                } catch (IOException e) {
                    logger.error("Error while sending stats to ListCord!", e);
                    Sentry.capture(e);
                }
            });
        }
    }

    private void sendDBots(Settings settings) {
        if (settings.apiKeys.containsKey("dbots")) {
            shardManager.getShards().forEach(shard -> {
                RequestBody body = RequestBody.create(JSON,
                        "{\"server_count\":" + shard.getGuilds().size() + ", \"shard_id\":"
                                + shard.getShardInfo().getShardId() + ", \"shard_count\":"
                                + shard.getShardInfo().getShardTotal() + "}");
                Request request = new Request.Builder()
                        .header("Authorization", settings.apiKeys.get("dbots"))
                        .url("https://bots.discord.pw/api/bots/" + shard.getSelfUser().getId() + "/stats")
                        .post(body)
                        .build();
                try {
                    client.newCall(request).execute().close();
                } catch (IOException e) {
                    logger.error("Error while sending stats to Discord Bots!", e);
                    Sentry.capture(e);
                }
            });
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.MINUTES);
    }
}
