package me.gabixdev.kyoko;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.gabixdev.kyoko.command.BasicCommands;
import me.gabixdev.kyoko.command.MiscCommands;
import me.gabixdev.kyoko.command.basic.AbstractEmbedBuilder;
import me.gabixdev.kyoko.command.basic.DiscordCommands;
import me.gabixdev.kyoko.util.ColoredFormatter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import me.gabixdev.kyoko.util.command.Commands;
import org.fusesource.jansi.AnsiConsole;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class Kyoko {
    private final Settings settings;
    private final Commands commands;

    private final AbstractEmbedBuilder abstractEmbedBuilder;

    private final Cache<String, ExecutorService> poolCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    private JDA jda;
    private Logger log;

    private boolean running;

    public Kyoko(Settings settings) {
        this.settings = settings;
        this.commands = new DiscordCommands(this);
        this.abstractEmbedBuilder = new AbstractEmbedBuilder(this);
    }

    public void start() throws LoginException, InterruptedException, RateLimitedException {
        running = true;

        // init logger
        AnsiConsole.systemInstall();
        log = Logger.getLogger("Kyoko");
        log.setUseParentHandlers(false);
        ColoredFormatter formatter = new ColoredFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        log.addHandler(handler);

        log.info("Kyoko v" + Constants.VERSION + " is starting...");

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        if (settings.getToken() != null) {
            if (settings.getToken().equalsIgnoreCase("Change me")) {
                System.out.println("No token specified, please set it in config.json");
                System.exit(1);
            }
            builder.setToken(settings.getToken());
        }

        boolean gameEnabled = false;
        if (settings.getGame() != null && !settings.getGame().isEmpty()) {
            gameEnabled = true;
            builder.setGame(Game.of("booting..."));
        }

        builder.setAutoReconnect(true);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.addEventListener((DiscordCommands) commands);
        builder.setAudioEnabled(false);
        builder.setStatus(OnlineStatus.IDLE);
        jda = builder.buildBlocking();

        log.info("Invite link: " + "https://discordapp.com/oauth2/authorize?&client_id=" + jda.getSelfUser().getId() + "&scope=bot&permissions=" + Constants.PERMISSIONS);

        if (gameEnabled) {
            Thread t = new Thread(new Kyoko.BlinkThread());
            t.start();
        }

        commands();
    }

    private void commands() {
        commands.registerCommandObjects(
                new BasicCommands(this),
                new MiscCommands(this)
        );
    }

    public void run(Guild guild, Runnable runnable) {
        if (guild == null || runnable == null) {
            return;
        }
        ExecutorService service = poolCache.getIfPresent(guild.getId());
        if (service == null) {
            service = new ThreadPoolExecutor(2, 16, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
            poolCache.put(guild.getId(), service);
        }
        service.execute(runnable);
    }

    public Commands getCommands() {
        return commands;
    }

    public JDA getJda() {
        return jda;
    }

    public Settings getSettings() {
        return settings;
    }

    public AbstractEmbedBuilder getAbstractEmbedBuilder() {
        return abstractEmbedBuilder;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private class BlinkThread implements Runnable {

        @Override
        public void run() {
            switch (settings.getBlinkingShit().toLowerCase()) {
                case "4color":
                    log.info("Blinking shit set to \"4color\".");
                    while (running) {
                        try {
                            jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                            Thread.sleep(10000);
                            jda.getPresence().setStatus(OnlineStatus.ONLINE);
                            jda.getPresence().setGame(Game.of(settings.getGame(), "https://twitch.tv/#"));
                            Thread.sleep(10000);
                            jda.getPresence().setGame(Game.of(settings.getGame(), "https://gabixdev.me/#"));
                            Thread.sleep(10000);
                            jda.getPresence().setStatus(OnlineStatus.IDLE);
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                    }
                    break;
                case "redgreen":
                    log.info("Blinking shit set to \"redgreen\".");
                    jda.getPresence().setGame(Game.of(settings.getGame(), settings.getGameUrl()));
                    while (running) {
                        try {
                            jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                            Thread.sleep(2000);
                            jda.getPresence().setStatus(OnlineStatus.ONLINE);
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                    }
                    break;
                case "twitch":
                    log.info("Blinking shit set to \"twitch\".");
                    jda.getPresence().setGame(Game.of(settings.getGame(), "https://twitch.tv/#"));
                    break;
                default:
                    log.info("No blinking shit set.");
            }
        }
    }
}
