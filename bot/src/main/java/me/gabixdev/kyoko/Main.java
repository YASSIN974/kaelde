package me.gabixdev.kyoko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

public class Main {
    public static void main(String... args) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        File cfg = new File("config.json");
        if (!cfg.exists()) {
            try {
                cfg.createNewFile();
                Files.write(cfg.toPath(), gson.toJson(new Settings()).getBytes("UTF-8"));
                System.out.println("Configuration created, please setup the bot :)");
            } catch (Exception e) {
                System.out.println("Error creating default configuration!");
                e.printStackTrace();
            }
        } else {
            try {
                Settings settings = gson.fromJson(new FileReader(cfg), Settings.class);
                new Kyoko(settings);
            } catch (Exception e) {
                System.out.println("Oops, something went wrong!");
                e.printStackTrace();
            }
        }
    }
}
