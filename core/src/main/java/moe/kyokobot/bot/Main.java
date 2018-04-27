package moe.kyokobot.bot;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moe.kyokobot.bot.discordapi.DiscordAPI;
import moe.kyokobot.bot.discordapi.impl.JDADiscordAPI;
import moe.kyokobot.bot.services.GuildCountService;
import moe.kyokobot.bot.services.KyokoService;
import moe.kyokobot.bot.util.KyokoJDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.requests.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        if (settings.connection.token.isEmpty()) {
            logger.error("No token specified!");
            return;
        }

        DiscordAPI api = null;
        EventBus eventBus = new EventBus();

        if (settings.connection.mode.equalsIgnoreCase("gateway")) {

        } else {
            api = new JDADiscordAPI(settings.connection.token, eventBus);
        }

        try {
            api.initialize();

            Service kyoko = new KyokoService(settings, api, eventBus);
            Service guildCount = new GuildCountService(settings, api);

            ServiceManager serviceManager = new ServiceManager(asList(kyoko, guildCount));
            serviceManager.startAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
