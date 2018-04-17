package me.gabixdev.kyoko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

                    Files.write(cfg.toPath(), gson.toJson(settings).getBytes("UTF-8"));
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

        try {
            new KyokoService(settings).startUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
