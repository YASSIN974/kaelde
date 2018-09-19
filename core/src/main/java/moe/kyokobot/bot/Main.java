package moe.kyokobot.bot;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ServiceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.sentry.Sentry;
import moe.kyokobot.bot.event.ConnectedEvent;
import moe.kyokobot.bot.services.GuildCountService;
import moe.kyokobot.bot.services.KyokoService;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

public class Main {

    @SuppressWarnings("squid:S3776")
    public static void main(String... args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info(" __                 __           __           __                           ");
        logger.info("|  |--.--.--.-----.|  |--.-----.|  |--.-----.|  |_   .--------.-----.-----.");
        logger.info("|    <|  |  |  _  ||    <|  _  ||  _  |  _  ||   _|__|        |  _  |  -__|");
        logger.info("|__|__|___  |_____||__|__|_____||_____|_____||____|__|__|__|__|_____|_____|");
        logger.info("      |_____|                                                              ");
        logger.info("");
        logger.info("» Starting Kyoko v{}", Constants.VERSION);

        String cfgname = System.getenv("KYOKO_CONFIG");
        if (cfgname == null) cfgname = System.getProperty("kyoko.config");

        File cfg = new File(cfgname != null ? cfgname : "config.json");
        Settings settings = null;
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        if (!cfg.exists()) {
            try {
                if (cfg.createNewFile()) {
                    settings = new Settings();

                    Files.write(cfg.toPath(), gson.toJson(settings).getBytes(Charsets.UTF_8));
                    logger.info("Configuration created, please setup the bot :)");
                    System.exit(1);
                }
            } catch (Exception e) {
                logger.error("Error creating default configuration!", e);
                System.exit(1);
            }
        }

        try {
            settings = gson.fromJson(new FileReader(cfg), Settings.class);
        } catch (Exception e) {
            logger.error("Cannot read configuration file!", e);
            System.exit(1);
        }

        Settings.instance = settings;

        if (settings.connection.token.isEmpty()) {
            logger.error("No token specified!");
            System.exit(1);
            return;
        }

        if (settings.apiKeys.containsKey("sentry-dsn")) {
            logger.info("Enabling sentry support...");
            Sentry.init(settings.apiKeys.get("sentry-dsn"));
        }

        try {
            EventBus eventBus = new EventBus();
            JDAEventHandler eventHandler = new JDAEventHandler(eventBus);


            String[] shards = settings.connection.shardString.split(":");
            if (shards.length != 3) {
                logger.error("Invalid shard string, should be in format \"<min shard>:<max shard>:<total shard count>\"");
                System.exit(1);
            } else {
                int min = Integer.parseUnsignedInt(shards[0]);
                int max = Integer.parseUnsignedInt(shards[1]);
                int count = Integer.parseUnsignedInt(shards[2]);

                logger.info("Waiting for shards...");
                DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
                builder.setShardsTotal(count);
                builder.setShards(min, max);
                builder.setAudioEnabled(true);
                builder.setAudioSendFactory(new NativeAudioSendFactory());
                builder.setAutoReconnect(true);
                builder.setToken(settings.connection.token);
                builder.addEventListeners(eventHandler);
                ShardManager shardManager = builder.build();

                ServiceManager kyokoManager = new ServiceManager(ImmutableList.of(new KyokoService(shardManager, eventBus)));
                ServiceManager guildCountManager = new ServiceManager(ImmutableList.of(new GuildCountService(shardManager)));

                // wait for user info on any shard
                while(!shardManager.getShards().stream().anyMatch(shard -> shard.getSelfUser() != null)) {
                    Thread.sleep(100);
                }

                JDA shard = shardManager.getShards().stream()
                        .filter(s -> s.getSelfUser() != null).findAny().get();

                if (shard.getSelfUser().getId().equals("375750637540868107")) {
                    Globals.clientId = shard.getSelfUser().getIdLong();
                    Globals.inDiscordBotsServer = Globals.inKyokoServer = Globals.production = true;
                }

                shard.asBot().getApplicationInfo().queue(applicationInfo ->
                        Globals.owner = UserUtil.toDiscrim(applicationInfo.getOwner()));

                kyokoManager.startAsync();

                // Let other shards connect
                while(shardManager.getShards().stream().anyMatch(s -> s.getStatus() != JDA.Status.CONNECTED)) {
                    Thread.sleep(100);
                }

                if (shardManager.getGuildById("375752406727786498") != null)
                    Globals.inKyokoServer = true;

                guildCountManager.startAsync();

                logger.info("Connected!");
                eventBus.post(new ConnectedEvent());
            }
        } catch (Exception e) {
            logger.error("Oops, something went really wrong while starting Kyoko!", e);
            Sentry.capture(e);
            System.exit(1);
        }
    }
}
