package me.gabixdev.kyoko.bot;

import me.gabixdev.kyoko.bot.command.basic.HelpCommand;
import me.gabixdev.kyoko.bot.event.KyokoEventHandler;
import me.gabixdev.kyoko.bot.manager.CommandManager;
import me.gabixdev.kyoko.shared.KyokoLogger;
import me.gabixdev.kyoko.shared.Settings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class Kyoko {
    private Settings settings;
    private Logger logger;
    private JDA jda;

    private KyokoEventHandler eventHandler;
    private CommandManager commandManager;
    private ThreadPoolExecutor executor;

    private volatile boolean running;

    public Kyoko(JDA jda, Settings settings) {
        this.jda = jda;
        this.settings = settings;

        logger = new KyokoLogger().getLog();
        eventHandler = new KyokoEventHandler(this);
        commandManager = new CommandManager(this);
        // 32 concurrent commands / shard
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);
        running = true;
    }

    public void run() {
        logger.info("Kyoko v" + Constants.VERSION + " is starting...");

        registerCommands();

        jda.addEventListener(eventHandler);

        logger.info("Logged in as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator() + " (" + jda.getSelfUser().getId() + ")");

        if (jda.getShardInfo() != null) {
            jda.getPresence().setGame(Game.listening(settings.normalPrefix + "help | Shard " + (jda.getShardInfo().getShardId() + 1) + " / " + jda.getShardInfo().getShardTotal()));
        } else {
            jda.getPresence().setGame(Game.listening(settings.normalPrefix + "help | " + jda.getGuilds().size() + " guilds"));
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            try {
                //System.out.print("> ");
                String line = br.readLine();
                String[] args = line.split(" ");
                if (args.length != 0) {
                    switch (args[0].toLowerCase()) {
                        case "reload":
                            logger.info("Reloading bot...");
                            running = false;
                            break;
                        case "exit":
                            logger.info("Bye!");
                            System.exit(0);
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

    private void registerCommands() {
        commandManager.registerCommand(new HelpCommand(this));
    }

    public JDA getJda() {
        return jda;
    }

    public Settings getSettings() {
        return settings;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
