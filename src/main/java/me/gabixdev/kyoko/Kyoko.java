package me.gabixdev.kyoko;

import com.github.natanbc.weeb4j.TokenType;
import com.github.natanbc.weeb4j.Weeb4J;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashBiMap;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import me.gabixdev.kyoko.command.AliasCommand;
import me.gabixdev.kyoko.command.basic.HelpCommand;
import me.gabixdev.kyoko.command.basic.InviteCommand;
import me.gabixdev.kyoko.command.basic.LangCommand;
import me.gabixdev.kyoko.command.fun.*;
import me.gabixdev.kyoko.command.images.*;
import me.gabixdev.kyoko.command.moderation.*;
import me.gabixdev.kyoko.command.money.DailiesCommand;
import me.gabixdev.kyoko.command.money.MoneyCommand;
import me.gabixdev.kyoko.command.money.MoneyTopCommand;
import me.gabixdev.kyoko.command.money.SendMoneyCommand;
import me.gabixdev.kyoko.command.music.*;
import me.gabixdev.kyoko.command.util.*;
import me.gabixdev.kyoko.database.DatabaseManager;
import me.gabixdev.kyoko.i18n.I18n;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.music.NicoAudioSourceManager;
import me.gabixdev.kyoko.music.YoutubeSearch;
import me.gabixdev.kyoko.util.APICommands;
import me.gabixdev.kyoko.util.ColoredFormatter;
import me.gabixdev.kyoko.util.KyokoJDABuilder;
import me.gabixdev.kyoko.util.command.AbstractEmbedBuilder;
import me.gabixdev.kyoko.util.command.CommandCategory;
import me.gabixdev.kyoko.util.command.CommandManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.tuple.MutableTriple;
import org.discordbots.api.client.DiscordBotListAPI;
import org.fusesource.jansi.AnsiConsole;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class Kyoko {
    private Settings settings;
    private final EventHandler eventHandler;
    private final CommandManager commandManager;
    private final DatabaseManager databaseManager;
    private final I18n i18n;

    private final AbstractEmbedBuilder abstractEmbedBuilder;
    private ScriptEngine scriptEngine;

    private final Cache<String, ExecutorService> poolCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private ThreadPoolExecutor executor;
    private final AudioPlayerManager playerManager;
    private final Map<Long, MusicManager> musicManagers;
    public String supportedSources;
    private Thread blinkThread;

    private JDA jda;
    private Logger log;
    private Weeb4J weeb4j;
    private DiscordBotListAPI dblApi;

    private boolean running;
    private boolean initialized;

    public Kyoko(Settings settings) {
        this.settings = settings;
        eventHandler = new EventHandler(this);
        abstractEmbedBuilder = new AbstractEmbedBuilder(this);
        commandManager = new CommandManager(this);
        databaseManager = new DatabaseManager(this);
        i18n = new I18n(this);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(32);

        musicManagers = new HashMap<>();

        playerManager = new DefaultAudioPlayerManager();
        playerManager.setFrameBufferDuration(3000);
        playerManager.getConfiguration().setFilterHotSwapEnabled(true);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());

        supportedSources = "YouTube, SoundCloud, Bandcamp, Vimeo, Twitch";

        if (settings.isNicovideoEnabled()) {
            playerManager.registerSourceManager(new NicoAudioSourceManager(settings.getNicoMail(), settings.getNicoPassword()));
            supportedSources += ", NicoNico";
        }

        if (settings.isAllowUnsafeSources()) {
            playerManager.registerSourceManager(new HttpAudioSourceManager());
            //playerManager.registerSourceManager(new LocalAudioSourceManager());
            //supportedSources += ", direct HTTP link, local filesystem";
        }

        if (!settings.getWeebshApiKey().equals("ask wolke")) {
            weeb4j = new Weeb4J.Builder().setToken(TokenType.WOLKE, settings.getWeebshApiKey()).build();
        }

        if (!settings.getDblApiKey().equals("DiscordBotList API key")) {
             dblApi = new DiscordBotListAPI.Builder().token(settings.getDblApiKey()).build();
        }
    }

    public void start() throws LoginException, InterruptedException {
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

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.severe("Cannot find MySQL JDBC driver!");
            e.printStackTrace();
            running = false;
            return;
        }

        i18n.loadMessages();
        databaseManager.load(settings);

        KyokoJDABuilder builder = new KyokoJDABuilder(AccountType.BOT);
        if (settings.getToken() != null) {
            if (settings.getToken().equalsIgnoreCase("Change me")) {
                log.severe("No token specified, please set it in config.json");
                running = false;
                return;
            }
            builder.setToken(settings.getToken());
        }

        //builder.setGateway("wss://localhost:8000");

        builder.setAutoReconnect(true)
                .setBulkDeleteSplittingEnabled(false)
                .addEventListener(eventHandler)
                .setAudioEnabled(true)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .setStatus(OnlineStatus.IDLE);
        jda = builder.buildBlocking();

        if (dblApi != null) dblApi.setStats(jda.getSelfUser().getId(), jda.getGuilds().size());

        log.info("Invite link: " + "https://discordapp.com/oauth2/authorize?&client_id=" + jda.getSelfUser().getId() + "&scope=bot&permissions=" + Constants.PERMISSIONS);

        if (settings.getGame() != null && !settings.getGame().isEmpty()) {
            blinkThread = new Thread(new BlinkThread(this));
            blinkThread.start();
        }

        registerCommands();
        initJS();

        //System.setProperty("kyoko.apicommand", "avatarUpdate");
        if (System.getProperty("kyoko.apicommand") != null) {
            APICommands.execCommand(this);
            running = false;
            return;
        }

        initialized = true;
    }

    private void registerCommands() {
        // basic
        commandManager.registerCommand(new HelpCommand(this));
        commandManager.registerCommand(new InviteCommand(this));
        commandManager.registerCommand(new LangCommand(this));

        // fun
        commandManager.registerCommand(new BannerCommand(this));
        commandManager.registerCommand(new BananaCommand(this));
        commandManager.registerCommand(new FigletCommand(this));
        commandManager.registerCommand(new SpinnerCommand(this));
        commandManager.registerCommand(new KysCommand(this));
        commandManager.registerCommand(new SaucenaoCommand(this));

        // images
        if (weeb4j != null) {
            //commandManager.registerCommand(new TrapCommand(this));
            commandManager.registerCommand(new CatCommand(this));
            commandManager.registerCommand(new HugCommand(this));
            commandManager.registerCommand(new PatCommand(this));
            commandManager.registerCommand(new PunchCommand(this));
            commandManager.registerCommand(new SlapCommand(this));
            commandManager.registerCommand(new LickCommand(this));
            commandManager.registerCommand(new WeebshCommand(this));
            commandManager.registerCommand(new ShipCommand(this));

            commandManager.registerCommand(new AliasCommand(this, "waaa", "weebsh.description.waaa", null, CommandCategory.IMAGES, new String[] {"weebsh", "cry"}));
            commandManager.registerCommand(new AliasCommand(this, "discordmeme", "weebsh.description.discordmeme", null, CommandCategory.IMAGES, new String[] {"weebsh", "discord_memes"}));
            commandManager.registerCommand(new AliasCommand(this, "dance", "weebsh.description.dance", null, CommandCategory.IMAGES, new String[] {"weebsh", "dance"}));
            commandManager.registerCommand(new AliasCommand(this, "insult", "weebsh.description.insult", null, CommandCategory.IMAGES, new String[] {"weebsh", "insult"}));
            commandManager.registerCommand(new AliasCommand(this, "initiald", "weebsh.description.initiald", null, CommandCategory.IMAGES, new String[] {"weebsh", "initial_d"}));
            commandManager.registerCommand(new AliasCommand(this, "trap", "weebsh.description.trap", null, CommandCategory.IMAGES, new String[] {"weebsh", "trap"}));
            commandManager.registerCommand(new AliasCommand(this, "kemonomimi", "weebsh.description.kemonomimi", null, CommandCategory.IMAGES, new String[] {"weebsh", "kemonomimi"}));
            commandManager.registerCommand(new AliasCommand(this, "triggered", "weebsh.description.triggered", null, CommandCategory.IMAGES, new String[] {"weebsh", "triggered"}));
            commandManager.registerCommand(new AliasCommand(this, "poi", "weebsh.description.poi", null, CommandCategory.IMAGES, new String[] {"weebsh", "poi"}));
            commandManager.registerCommand(new AliasCommand(this, "neko", "weebsh.description.neko", null, CommandCategory.IMAGES, new String[] {"weebsh", "neko"}));
            commandManager.registerCommand(new AliasCommand(this, "megumin", "weebsh.description.megumin", null, CommandCategory.IMAGES, new String[] {"weebsh", "megumin"}));
        }

        commandManager.registerCommand(new DogCommand(this));
        commandManager.registerCommand(new NekosCommand(this));
        commandManager.registerCommand(new AliasCommand(this, "lizard", "nekos.description.lizard", null, CommandCategory.IMAGES, new String[] {"nekos", "lizard"}));
        commandManager.registerCommand(new AliasCommand(this, "lewdneko", "nekos.description.lewdneko", null, CommandCategory.IMAGES, new String[] {"nekos", "lewd"}));

        // utils
        commandManager.registerCommand(new PingCommand(this));
        commandManager.registerCommand(new SayCommand(this));
        commandManager.registerCommand(new StatsCommand(this));
        commandManager.registerCommand(new CryptoTopCommand(this));
        commandManager.registerCommand(new Base64Command(this));
        commandManager.registerCommand(new UnBase64Command(this));
        commandManager.registerCommand(new AvatarCommand(this));
        commandManager.registerCommand(new UserInfoCommand(this));
        commandManager.registerCommand(new VoteCommand(this));

        // money
        commandManager.registerCommand(new MoneyCommand(this));
        commandManager.registerCommand(new MoneyTopCommand(this));
        commandManager.registerCommand(new DailiesCommand(this));
        commandManager.registerCommand(new SendMoneyCommand(this));

        // moderation
        commandManager.registerCommand(new PruneCommand(this));
        commandManager.registerCommand(new KickCommand(this));
        commandManager.registerCommand(new BanCommand(this));
        commandManager.registerCommand(new UnbanCommand(this));
        commandManager.registerCommand(new PrefixCommand(this));

        // music
        commandManager.registerCommand(new JoinCommand(this));
        commandManager.registerCommand(new PlayCommand(this));
        commandManager.registerCommand(new SkipCommand(this));
        commandManager.registerCommand(new ClearCommand(this));
        commandManager.registerCommand(new ListCommand(this));
        commandManager.registerCommand(new StopCommand(this));
        commandManager.registerCommand(new PauseCommand(this));
        commandManager.registerCommand(new VolumeCommand(this));
        commandManager.registerCommand(new ShuffleCommand(this));
        commandManager.registerCommand(new NightcoreCommand(this));
        commandManager.registerCommand(new SpeedCommand(this));

        if (settings.isYoutubeSearchEnabled()) {
            new YoutubeSearch(this);
            commandManager.registerCommand(new PlayYoutubeCommand(this));
        }

        if (settings.isWipFeaturesEnabled()) {
            commandManager.registerCommand(new DecancerCommand(this));
        }
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

    public void initJS() {
        scriptEngine = new ScriptEngineManager().getEngineByName("js");
        if (settings.isEvalEnabled()) {
            try {
                scriptEngine.eval("load(\"nashorn:mozilla_compat.js\");importPackage(java.lang);importPackage(java.io);importPackage(java.util);");
                scriptEngine.put("kyoko", this);
            } catch (ScriptException ex) {
                ex.printStackTrace();
            }
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Thread getBlinkThread() {
        return blinkThread;
    }

    public void setBlinkThread(Thread blinkThread) {
        this.blinkThread = blinkThread;
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

    public void setSettings(Settings settings) {
        this.settings = settings;
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
            return "[0/1]";
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

    public boolean isRunning() {
        return running;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public Weeb4J getWeeb4j() {
        return weeb4j;
    }

    public DiscordBotListAPI getDblApi() {
        return dblApi;
    }
}
