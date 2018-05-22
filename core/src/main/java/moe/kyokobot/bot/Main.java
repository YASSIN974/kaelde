package moe.kyokobot.bot;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sentry.Sentry;
import moe.kyokobot.bot.services.GuildCountService;
import moe.kyokobot.bot.services.KyokoService;
import moe.kyokobot.bot.util.KyokoJDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.EventHandler;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Arrays;

import static java.util.Arrays.asList;

public class Main {
    public static void main(String... args) {
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
                logger.error("Error creating default configuration!");
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            settings = gson.fromJson(new FileReader(cfg), Settings.class);
        } catch (Exception e) {
            logger.error("Cannot read configuration file!");
            e.printStackTrace();
            System.exit(1);
        }

        Constants.DEBUG = settings.debug;

        if (settings.connection.token.isEmpty()) {
            logger.error("No token specified!");
            return;
        }

        if (settings.apiKeys.containsKey("sentry-dsn")) {
            Sentry.init(settings.apiKeys.get("sentry-dsn"));
        }

        KyokoJDABuilder jdaBuilder = new KyokoJDABuilder(AccountType.BOT);

        if (settings.connection.mode.equalsIgnoreCase("gateway")) {
            jdaBuilder.setGateway(settings.connection.gatewayServer);
            Requester.DISCORD_API_PREFIX = settings.connection.restServer;
        }

        jdaBuilder.setAudioEnabled(true);
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.setToken(settings.connection.token);

        try {
            EventBus eventBus = new EventBus();
            JDAEventHandler eventHandler = new JDAEventHandler(eventBus);
            jdaBuilder.addEventListener(eventHandler);
            JDA jda = jdaBuilder.buildBlocking();
            Globals.clientId = jda.getSelfUser().getIdLong();

            Service kyoko = new KyokoService(settings, jda, eventBus);
            Service guildCount = new GuildCountService(settings, jda);

            ServiceManager serviceManager = new ServiceManager(asList(kyoko, guildCount));
            serviceManager.startAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
