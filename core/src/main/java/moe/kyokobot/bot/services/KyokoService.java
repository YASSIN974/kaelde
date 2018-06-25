package moe.kyokobot.bot.services;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import io.sentry.Sentry;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;
import moe.kyokobot.bot.manager.impl.ExternalModuleManager;
import moe.kyokobot.bot.manager.impl.KyokoCommandManager;
import moe.kyokobot.bot.manager.impl.RethinkDatabaseManager;
import moe.kyokobot.bot.util.EventWaiter;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KyokoService extends AbstractIdleService {
    private final Logger logger;
    private ModuleManager moduleManager;
    private RethinkDatabaseManager databaseManager;
    private CommandManager commandManager;
    private EventWaiter eventWaiter;
    private I18n i18n;

    private boolean sharded;
    private JDA jda;
    private ShardManager shardManager;

    public KyokoService(JDA jda, EventBus eventBus) {
        this(eventBus);
        this.jda = jda;

        moduleManager = new ExternalModuleManager(null, databaseManager, i18n, commandManager, eventWaiter);
        eventBus.register(moduleManager);
    }

    public KyokoService(ShardManager shardManager, EventBus eventBus) {
        this(eventBus);
        sharded = true;
        this.shardManager = shardManager;

        moduleManager = new ExternalModuleManager(shardManager, databaseManager, i18n, commandManager, eventWaiter);
        eventBus.register(moduleManager);
    }

    private KyokoService(EventBus eventBus) {
        logger = LoggerFactory.getLogger(getClass());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
        eventWaiter = new EventWaiter();

        databaseManager = new RethinkDatabaseManager();
        i18n = new I18n(databaseManager);
        commandManager = new KyokoCommandManager(i18n, executor);

        eventBus.register(commandManager);
        eventBus.register(databaseManager);
    }

    @Override
    public void startUp() throws Exception {
        try {
            logger.debug("Starting Kyoko service...");
            databaseManager.load();
            moduleManager.loadModules();

            if (sharded) {
                shardManager.addEventListener(eventWaiter);
            } else {
                jda.addEventListener(eventWaiter);
            }
        } catch (Exception e) {
            logger.error("Oops, something went really wrong while starting Kyoko!", e);
            Sentry.capture(e);
            this.stopAsync();
            System.exit(1);
        }
    }

    @Override
    public void shutDown() throws Exception {
        if (jda != null) jda.shutdown();
        if (shardManager != null) shardManager.shutdown();
    }
}
