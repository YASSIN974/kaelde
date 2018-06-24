package moe.kyokobot.bot;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import io.sentry.Sentry;
import moe.kyokobot.bot.services.GuildCountService;
import moe.kyokobot.bot.services.KyokoService;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main {
    public static void main(String... args) {
        boolean debug = System.getProperty("kyoko.debug", "false").equalsIgnoreCase("true");
        if (debug) System.setProperty("logback.configurationFile", "/debug.xml");

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info(" __                 __           __           __                           ");
        logger.info("|  |--.--.--.-----.|  |--.-----.|  |--.-----.|  |_   .--------.-----.-----.");
        logger.info("|    <|  |  |  _  ||    <|  _  ||  _  |  _  ||   _|__|        |  _  |  -__|");
        logger.info("|__|__|___  |_____||__|__|_____||_____|_____||____|__|__|__|__|_____|_____|");
        logger.info("      |_____|                                                              ");
        logger.info("KyokoBot is loading...");

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

        if (debug) settings.debug = true;
        Constants.DEBUG = settings.debug;

        if (settings.connection.token.isEmpty()) {
            logger.error("No token specified!");
            return;
        }

        if (settings.apiKeys.containsKey("sentry-dsn")) {
            logger.info("Enabling sentry support...");
            Sentry.init(settings.apiKeys.get("sentry-dsn"));
        }

        ArrayList<Service> services = new ArrayList<>();

        try {
            EventBus eventBus = new EventBus();
            JDAEventHandler eventHandler = new JDAEventHandler(eventBus);


            if (settings.connection.mode.equalsIgnoreCase("single")) {
                JDABuilder builder = new JDABuilder(AccountType.BOT);
                builder.setAudioEnabled(true);
                builder.setAutoReconnect(true);
                builder.setAudioSendFactory(new NativeAudioSendFactory());
                builder.setToken(settings.connection.token);
                builder.addEventListener(eventHandler);
                JDA jda = builder.buildBlocking();

                if (jda.getGuildById("375752406727786498") != null) Globals.inKyokoServer = true;

                Globals.clientId = jda.getSelfUser().getIdLong();

                services.add(new KyokoService(settings, jda, eventBus));
                services.add(new GuildCountService(settings, jda));
            } else if (settings.connection.mode.equalsIgnoreCase("sharded")) {
                String[] shards = settings.connection.shardString.split(":");
                if (shards.length != 3) {
                    logger.error("Invalid shard string, should be in format \"<min shard>:<max shard>:<total shard count>\"");
                    System.exit(1);
                    return;
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

                    while(shardManager.getShards().stream().anyMatch(shard -> shard.getStatus() != JDA.Status.CONNECTED)) {
                        Thread.sleep(1000);
                    }
                    logger.info("Connected!");

                    Globals.sharded = true;
                    if (shardManager.getGuildById("375752406727786498") != null) Globals.inKyokoServer = true;

                    services.add(new KyokoService(settings, shardManager, eventBus));
                }
            } else {
                logger.error("Unknown connection mode, valid values are: single, sharded");
                System.exit(1);
                return;
            }

            logger.info("Setup complete, starting!");
            ServiceManager serviceManager = new ServiceManager(services);
            serviceManager.addListener(new ServiceManager.Listener() {
                @Override
                public void failure(Service service) {
                    logger.error("Service " + service.getClass().getName() + "failed!", service.failureCause());
                }
            });
            serviceManager.startAsync();

        } catch (Exception e) {
            logger.error("Oops, something went really wrong while starting Kyoko!", e);
            Sentry.capture(e);
            System.exit(1);
        }
    }
}
