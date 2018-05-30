package moe.kyokobot.bot.services;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import io.sentry.Sentry;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.impl.ExternalModuleManager;
import moe.kyokobot.bot.manager.impl.RethinkDatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;
import moe.kyokobot.bot.manager.impl.KyokoCommandManager;
import moe.kyokobot.bot.util.EventWaiter;
import net.dv8tion.jda.core.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KyokoService extends AbstractIdleService {
    private final Logger logger;
    private JDA jda;
    private ModuleManager moduleManager;
    private RethinkDatabaseManager databaseManager;
    private CommandManager commandManager;
    private EventWaiter eventWaiter;
    private I18n i18n;

    public KyokoService(Settings settings, JDA jda, EventBus eventBus) {
        logger = LoggerFactory.getLogger(getClass());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
        eventWaiter = new EventWaiter();
        this.jda = jda;

        databaseManager = new RethinkDatabaseManager(settings);
        i18n = new I18n(databaseManager);
        commandManager = new KyokoCommandManager(settings, i18n, executor);
        moduleManager = new ExternalModuleManager(settings, databaseManager, i18n, commandManager, eventWaiter);

        eventBus.register(commandManager);
        eventBus.register(databaseManager);
        eventBus.register(moduleManager);
    }

    @Override
    public void startUp() throws Exception {
        try {
            logger.debug("Starting Kyoko service...");
            databaseManager.load();
            moduleManager.loadModules();
            jda.addEventListener(eventWaiter);
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
