package moe.kyokobot.bot.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import io.sentry.Sentry;
import jdk.nashorn.internal.objects.Global;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.event.GuildCountUpdateEvent;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import okhttp3.*;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GuildCountService extends AbstractScheduledService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final Logger logger = LoggerFactory.getLogger(GuildCountService.class);
    private JDA jda;
    private ShardManager shardManager;

    private OkHttpClient client = new OkHttpClient();
    private DiscordBotListAPI dblAPI;
    private int last = 0;

    public GuildCountService(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    public GuildCountService(JDA jda) {
        this.jda = jda;
    }

    @Override
    protected void runOneIteration() throws Exception {
        Settings settings = Settings.instance;

        if (dblAPI == null && settings.apiKeys.containsKey("dbl")) {
            dblAPI = new DiscordBotListAPI.Builder().token(settings.apiKeys.get("dbl")).build();
        }

        setPresence(settings);
        sendStats(settings);
    }

    private void setPresence(Settings settings) {
        if (settings.bot.games.isEmpty()) return;
        if (last >= settings.bot.games.size()) last = 0;

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
                            .replace("{guilds}", String.valueOf(jda.getGuilds().size()))));
            last++;
        }
    }

    private void sendStats(Settings settings) {
        if (dblAPI != null) {
            shardManager.getShards().forEach(shard -> dblAPI.setStats(shard.getSelfUser().getId(),
                    shard.getGuilds().size(), shard.getShardInfo().getShardId(), shard.getShardInfo().getShardTotal()));
        }

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
                    client.newCall(request).execute();
                } catch (IOException e) {
                    logger.error("Error while sending stats to ListCord!", e);
                    Sentry.capture(e);
                }
            });
        }

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
                    client.newCall(request).execute();
                } catch (IOException e) {
                    logger.error("Error while sending stats to Discord Bots!", e);
                    Sentry.capture(e);
                }
            });
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 2, TimeUnit.MINUTES);
    }
}
