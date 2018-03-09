package me.gabixdev.kyoko.bot;

import me.gabixdev.kyoko.bot.event.KyokoEventHandler;
import me.gabixdev.kyoko.shared.KyokoLogger;
import me.gabixdev.kyoko.shared.Settings;
import net.dv8tion.jda.core.JDA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Kyoko {
    private Settings settings;
    private Logger logger;
    private JDA jda;
    private KyokoEventHandler eventHandler;

    private volatile boolean running;

    public Kyoko(JDA jda, Settings settings) {
        this.jda = jda;
        this.settings = settings;
        this.logger = new KyokoLogger().getLog();
        this.eventHandler = new KyokoEventHandler(this);
        running = true;
    }

    public void run() {
        logger.info("Kyoko v" + Constants.VERSION + " is starting...");

        jda.addEventListener(eventHandler);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            try {
                System.out.print("> ");
                String line = br.readLine();
                String[] args = line.split(" ");
                if (args.length != 0) {
                    switch (args[0].toLowerCase()) {
                        case "reload":
                            logger.info("Reload is coming :3");
                            running = false;
                            break;
                        case "test":
                            logger.info(":)");
                            break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        logger.info("Shutting down...");

        jda.removeEventListener(eventHandler);
    }
}
