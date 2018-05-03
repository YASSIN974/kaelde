package moe.kyokobot.bot.services;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import io.sentry.Sentry;
import moe.kyokobot.bot.JDAEventHandler;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.debug.ModulesCommand;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.impl.CommandManagerImpl;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.KyokoJDABuilder;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.requests.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class KyokoService extends AbstractIdleService {
    private final Logger logger;
    private JDA jda;

    private ScheduledExecutorService executor;
    private ModuleManager moduleManager;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private EventWaiter eventWaiter;
    private JDAEventHandler eventHandler;
    private EventBus eventBus;
    private I18n i18n;
    private Settings settings;

    public KyokoService(Settings settings, JDA jda) {
        logger = LoggerFactory.getLogger(getClass());
        executor = (ScheduledExecutorService) Executors.newScheduledThreadPool(4);
        eventBus = new EventBus();
        eventWaiter = new EventWaiter();

        this.settings = settings;
        this.jda = jda;

        databaseManager = new DatabaseManager();
        i18n = new I18n(databaseManager);
        commandManager = new CommandManagerImpl(settings, i18n, executor);
        eventHandler = new JDAEventHandler(commandManager);
        moduleManager = new ModuleManager(settings, databaseManager, i18n, commandManager, eventWaiter);
    }

    @Override
    public void startUp() throws Exception {
        try {
            jda.addEventListener(eventHandler);
            jda.addEventListener(eventWaiter);
            databaseManager.load(settings);
            moduleManager.loadModules();
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
            this.stopAsync();
        }
    }

    @Override
    public void shutDown() throws Exception {
        if (jda != null) jda.shutdown();
    }
}
