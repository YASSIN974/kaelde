package me.gabixdev.kyoko.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.gabixdev.kyoko.shared.Settings;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;

public class Main {
    private static final String BOT_CLASS = "me.gabixdev.kyoko.bot.Kyoko";

    private static final File cfg = new File("config.json");
    private static final File kyoko_bot_jar = new File("kyoko-bot.jar");
    private static final File kyoko_bot_tree = new File("bot/target/classes");

    private static Thread kyokoThread;
    private static Settings settings;
    private static JDA jda;

    public static void main(String... args) {
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

        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT);

        if (settings.shardMode.equalsIgnoreCase("main") || settings.shardMode.equalsIgnoreCase("slave"))
            jdaBuilder.useSharding(settings.shardId, settings.shardCount);

        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.setBulkDeleteSplittingEnabled(false);
        jdaBuilder.setToken(settings.token);
        jdaBuilder.setAudioEnabled(true);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);

        try {
            jda = jdaBuilder.buildBlocking();
        } catch (Exception e) {
            System.out.println("Cannot login to Discord, invalid token?");
            e.printStackTrace();
            return;
        }

        kyokoThread = new Thread(() -> {
            boolean update = true;
            while (update) {
                // clean up listeners (if any)
                jda.getRegisteredListeners().forEach(jda::removeEventListener);

                try {
                    DelegateURLClassLoader kyokoClassLoader = new DelegateURLClassLoader(new URL[]{
                            kyoko_bot_jar.toURI().toURL(),
                            kyoko_bot_tree.toURI().toURL()
                    }, ClassLoader.getSystemClassLoader());

                    Class kyClass = Class.forName(BOT_CLASS, true, kyokoClassLoader);
                    Object kyoko = kyClass.getConstructor(JDA.class, Settings.class).newInstance(jda, settings);
                    Method runMethod = kyClass.getDeclaredMethod("run");

                    try {
                        runMethod.invoke(kyoko);
                    } catch (Exception e) {
                        System.out.println("Error while running Kyoko, retrying in 10 seconds...");
                        e.printStackTrace();
                        Thread.sleep(10000);
                    }
                } catch (Exception e) {
                    update = false;
                    System.out.println("Oops, something gone wrong!");
                    e.printStackTrace();
                }

                System.gc(); // remove classloader from memory
            }
        }, "Kyoko main thread");
        kyokoThread.start();
    }
}
