package moe.kyokobot.bot;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moe.kyokobot.bot.services.KyokoService;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

public class Main {
    public static void main(String... args) {
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
                    System.out.println("Configuration created, please setup the bot :)");
                    System.exit(1);
                }
            } catch (Exception e) {
                System.out.println("Error creating default configuration!");
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            settings = gson.fromJson(new FileReader(cfg), Settings.class);
        } catch (Exception e) {
            System.out.println("Cannot read configuration file!");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("KyokoBot is loading...");

        if (settings.connection.token.isEmpty()) {
            System.out.println("No token specified!");
            return;
        }

        try {
            Service kyoko = new KyokoService(settings).startAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
