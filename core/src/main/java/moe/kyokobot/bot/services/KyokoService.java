package moe.kyokobot.bot.services;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import moe.kyokobot.bot.discordapi.impl.JDAEventHandler;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.discordapi.DiscordAPI;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.impl.CommandManagerImpl;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KyokoService extends AbstractIdleService {
    private final Logger logger;

    private DiscordAPI discordAPI;

    private ScheduledExecutorService executor;
    private ModuleManager moduleManager;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private JDAEventHandler eventHandler;
    private EventBus eventBus;
    private I18n i18n;
    private Settings settings;

    public KyokoService(Settings settings, DiscordAPI discordAPI, EventBus eventBus) {
        logger = LoggerFactory.getLogger(getClass());
        executor = (ScheduledExecutorService) Executors.newScheduledThreadPool(4);

        this.settings = settings;
        this.discordAPI = discordAPI;
        this.eventBus = eventBus;

        databaseManager = new DatabaseManager();
        i18n = new I18n(databaseManager);
        commandManager = new CommandManagerImpl(settings, i18n, executor);
        eventHandler = new JDAEventHandler(eventBus);
        moduleManager = new ModuleManager(settings, commandManager);
    }

    @Override
    public void startUp() throws Exception {
        try {
            databaseManager.load(settings);
            i18n.loadMessages();
            moduleManager.loadModules();
        } catch (Exception e) {
            e.printStackTrace();
            this.stopAsync();
        }
    }

    @Override
    public void shutDown() throws Exception {
        if (discordAPI != null) discordAPI.shutdown();
    }
}
