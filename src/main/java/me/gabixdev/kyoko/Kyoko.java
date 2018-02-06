package me.gabixdev.kyoko;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import me.gabixdev.kyoko.command.basic.HelpCommand;
import me.gabixdev.kyoko.command.basic.InviteCommand;
import me.gabixdev.kyoko.command.fun.*;
import me.gabixdev.kyoko.command.music.*;
import me.gabixdev.kyoko.command.util.CryptoTopCommand;
import me.gabixdev.kyoko.command.util.PingCommand;
import me.gabixdev.kyoko.command.util.SayCommand;
import me.gabixdev.kyoko.command.util.StatsCommand;
import me.gabixdev.kyoko.i18n.I18n;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.ColoredFormatter;
import me.gabixdev.kyoko.util.command.AbstractEmbedBuilder;
import me.gabixdev.kyoko.util.command.CommandManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.fusesource.jansi.AnsiConsole;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private final EventHandler eventHandler;
    private final CommandManager commandManager;
    private final I18n i18n;

    private final AbstractEmbedBuilder abstractEmbedBuilder;

    private final Cache<String, ExecutorService> poolCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private final AudioPlayerManager playerManager;
    private final Map<Long, MusicManager> musicManagers;

    private JDA jda;
    private Logger log;

    private boolean running;
    private boolean initialized;

    public Kyoko(Settings settings) {
        this.settings = settings;
        this.eventHandler = new EventHandler(this);
        this.abstractEmbedBuilder = new AbstractEmbedBuilder(this);
        this.commandManager = new CommandManager(this);
        this.i18n = new I18n(this);

        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

        if (settings.isNicovideoEnabled()) {
            playerManager.registerSourceManager(new NicoAudioSourceManager(settings.getNicoMail(), settings.getNicoPassword()));
        }

        if (settings.isAllowUnsafeSources()) {
            playerManager.registerSourceManager(new HttpAudioSourceManager());
            playerManager.registerSourceManager(new LocalAudioSourceManager());
        }
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

        i18n.loadMessages();

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
            builder.setGame(Game.of(Game.GameType.DEFAULT, "booting..."));
        }

        builder.setAutoReconnect(true);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.addEventListener(eventHandler);
        builder.setAudioEnabled(true);
        builder.setStatus(OnlineStatus.IDLE);
        jda = builder.buildBlocking();

        log.info("Invite link: " + "https://discordapp.com/oauth2/authorize?&client_id=" + jda.getSelfUser().getId() + "&scope=bot&permissions=" + Constants.PERMISSIONS);

        if (gameEnabled) {
            Thread t = new Thread(new Kyoko.BlinkThread());
            t.start();
        }

        registerCommands();

        if (System.getProperty("kyoko.icommand", "").equalsIgnoreCase("avatarUpdate")) {
            File f = new File("avatar.png");
            if (f.exists()) {
                try {
                    jda.getSelfUser().getManager().setAvatar(Icon.from(f)).complete();
                    log.info("Avatar changed!");
                } catch (IOException e) {
                    log.severe("Can't read avatar file!");
                    e.printStackTrace();
                }
            } else {
                log.warning("Requested avatar change, but file does not exists. Place it as \"avatar.png\"");
            }
            System.exit(0);
            return;
        } else if (System.getProperty("kyoko.icommand", "").equalsIgnoreCase("nameUpdate")) {
            jda.getSelfUser().getManager().setName(System.getProperty("kyoko.newname", "Kyoko")).queue();
            log.info("Name updated!");
            System.exit(0);
            return;
        } else if (System.getProperty("kyoko.icommand", "").equalsIgnoreCase("listGuilds")) {
            System.out.println("I am on " + jda.getGuilds().size() + " guilds:");
            for (Guild g : jda.getGuilds()) {
                System.out.println(g.getName() + " (" + g.getId() + ") " + g.getMembers().size() + " members");
            }
            System.exit(0);
            return;
        }

        initialized = true;
    }

    private void registerCommands() {
        commandManager.registerCommand(new HelpCommand(this));
        commandManager.registerCommand(new InviteCommand(this));

        commandManager.registerCommand(new BannerCommand(this));
        commandManager.registerCommand(new BananaCommand(this));
        commandManager.registerCommand(new CatCommand(this));
        commandManager.registerCommand(new FigletCommand(this));
        commandManager.registerCommand(new HugCommand(this));
        commandManager.registerCommand(new SpinnerCommand(this));

        commandManager.registerCommand(new PingCommand(this));
        commandManager.registerCommand(new SayCommand(this));
        commandManager.registerCommand(new StatsCommand(this));
        commandManager.registerCommand(new CryptoTopCommand(this));

        commandManager.registerCommand(new JoinCommand(this));
        commandManager.registerCommand(new PlayCommand(this));
        commandManager.registerCommand(new SkipCommand(this));
        commandManager.registerCommand(new ClearCommand(this));
        commandManager.registerCommand(new ListCommand(this));
        commandManager.registerCommand(new StopCommand(this));
        commandManager.registerCommand(new PauseCommand(this));
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

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JDA getJda() {
        return jda;
    }

    public Settings getSettings() {
        return settings;
    }

    public Logger getLog() {
        return log;
    }

    public I18n getI18n() {
        return i18n;
    }

    public AbstractEmbedBuilder getAbstractEmbedBuilder() {
        return abstractEmbedBuilder;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getShardInfo() {
        if (jda.getShardInfo() == null) {
            return "n/a";
        } else {
            return jda.getShardInfo().getShardString();
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public synchronized MusicManager getMusicManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        MusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new MusicManager(playerManager, guild, this);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
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
                            jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, settings.getGame(), "https://twitch.tv/#"));
                            Thread.sleep(10000);
                            jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, settings.getGame(), settings.getGameUrl()));
                            Thread.sleep(10000);
                            jda.getPresence().setStatus(OnlineStatus.IDLE);
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                    }
                    break;
                case "redyellow":
                    log.info("Blinking shit set to \"redyellow\".");
                    jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, settings.getGame(), settings.getGameUrl()));
                    while (running) {
                        try {
                            jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                            Thread.sleep(2000);
                            jda.getPresence().setStatus(OnlineStatus.IDLE);
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                    }
                    break;
                case "listening-ry":
                    log.info("Blinking shit set to \"listening-ry\".");
                    jda.getPresence().setGame(Game.of(Game.GameType.LISTENING, settings.getGame(), settings.getGameUrl()));
                    while (running) {
                        try {
                            jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                            Thread.sleep(2000);
                            jda.getPresence().setStatus(OnlineStatus.IDLE);
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // nothing
                        }
                    }
                    break;
                case "listening":
                    log.info("Blinking shit set to \"listening\".");
                    jda.getPresence().setGame(Game.of(Game.GameType.LISTENING, settings.getGame(), settings.getGameUrl()));
                    break;
                case "twitch":
                    log.info("Blinking shit set to \"twitch\".");
                    jda.getPresence().setGame(Game.of(Game.GameType.STREAMING, settings.getGame(), "https://twitch.tv/#"));
                    break;
                default:
                    log.info("No blinking shit set.");
            }
        }
    }
}
