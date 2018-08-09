package moe.kyokobot.bot.services;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import io.sentry.Sentry;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;
import moe.kyokobot.bot.manager.impl.RethinkDatabaseManager;
import moe.kyokobot.bot.manager.impl.SimpleCommandManager;
import moe.kyokobot.bot.manager.impl.SimpleModuleManager;
import moe.kyokobot.bot.util.EventWaiter;
import net.dv8tion.jda.bot.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class KyokoService extends AbstractIdleService {
    private final Logger logger;
    private ModuleManager moduleManager;
    private RethinkDatabaseManager databaseManager;
    private ShardManager shardManager;

    public KyokoService(ShardManager shardManager, EventBus eventBus) {
        logger = LoggerFactory.getLogger(getClass());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
        EventWaiter eventWaiter = new EventWaiter();

        this.shardManager = shardManager;
        databaseManager = new RethinkDatabaseManager(eventBus);
        I18n i18n = new I18n(databaseManager);
        CommandManager commandManager = new SimpleCommandManager(databaseManager, i18n, executor, eventBus);
        moduleManager = new SimpleModuleManager(shardManager, databaseManager, i18n, commandManager, eventWaiter);

        eventBus.register(eventWaiter);
        eventBus.register(commandManager);
        eventBus.register(databaseManager);
        eventBus.register(moduleManager);
        eventBus.register(i18n);
    }

    @Override
    public void startUp() throws Exception {
        try {
            logger.debug("Starting Kyoko service...");
            databaseManager.load();
            moduleManager.loadModules();
        } catch (Exception e) {
            logger.error("Oops, something went really wrong while starting Kyoko!", e);
            Sentry.capture(e);
            this.stopAsync();
            System.exit(1);
        }
    }

    @Override
    public void shutDown() throws Exception {
        if (shardManager != null)
            shardManager.shutdown();
    }
}
