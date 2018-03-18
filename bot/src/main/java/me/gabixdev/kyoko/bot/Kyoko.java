package me.gabixdev.kyoko.bot;

import me.gabixdev.kyoko.bot.command.basic.HelpCommand;
import me.gabixdev.kyoko.bot.command.basic.InviteCommand;
import me.gabixdev.kyoko.bot.command.debug.CleanCommand;
import me.gabixdev.kyoko.bot.command.debug.EvalCommand;
import me.gabixdev.kyoko.bot.command.debug.ShellCommand;
import me.gabixdev.kyoko.bot.command.fun.BananaCommand;
import me.gabixdev.kyoko.bot.command.util.*;
import me.gabixdev.kyoko.bot.event.KyokoEventHandler;
import me.gabixdev.kyoko.bot.i18n.I18n;
import me.gabixdev.kyoko.bot.manager.CommandManager;
import me.gabixdev.kyoko.bot.util.CLICommands;
import me.gabixdev.kyoko.bot.util.EventWaiter;
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

    private I18n i18n;
    private KyokoEventHandler eventHandler;
    private EventWaiter waiter;
    private CommandManager commandManager;
    private ThreadPoolExecutor executor;

    private volatile boolean running;

    public Kyoko(JDA jda, Settings settings) {
        this.jda = jda;
        this.settings = settings;

        logger = new KyokoLogger().getLog();
        eventHandler = new KyokoEventHandler(this);
        waiter = new EventWaiter();
        commandManager = new CommandManager(this);
        // 32 concurrent commands / shard
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);

        i18n = new I18n(this);
    }

    public void run() {
        running = true;

        logger.info("Kyoko v" + Constants.VERSION + " is starting...");
        i18n.init();

        registerCommands();

        logger.info("Logged in as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator() + " (" + jda.getSelfUser().getId() + ")");

        if (jda.getShardInfo() != null) {
            jda.getPresence().setGame(Game.listening(settings.normalPrefix + "help | Shard " + (jda.getShardInfo().getShardId() + 1) + " / " + jda.getShardInfo().getShardTotal()));
        } else {
            jda.getPresence().setGame(Game.listening(settings.normalPrefix + "help | " + jda.getGuilds().size() + " guilds"));
        }

        // listen for events when bot is initialized
        jda.addEventListener(eventHandler);
        jda.addEventListener(waiter);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        CLICommands.runHandler(br, this);

        logger.info("Shutting down...");

        jda.removeEventListener(eventHandler);
        jda.removeEventListener(waiter);
    }

    private void registerCommands() {
        commandManager.registerCommand(new HelpCommand(this));
        commandManager.registerCommand(new InviteCommand(this));

        commandManager.registerCommand(new BananaCommand(this));

        commandManager.registerCommand(new AvatarCommand(this));
        commandManager.registerCommand(new Base64Command(this));
        commandManager.registerCommand(new EmoteCommand(this));
        commandManager.registerCommand(new SayCommand(this));
        commandManager.registerCommand(new PingCommand(this));
        commandManager.registerCommand(new Unbase64Command(this));

        commandManager.registerCommand(new ShellCommand(this));
        commandManager.registerCommand(new EvalCommand(this));
        commandManager.registerCommand(new CleanCommand(this));
    }

    public JDA getJda() {
        return jda;
    }

    public Settings getSettings() {
        return settings;
    }

    public I18n getI18n() {
        return i18n;
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
