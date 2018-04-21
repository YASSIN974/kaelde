package moe.kyokobot.bot.services;

import com.google.common.util.concurrent.AbstractIdleService;
import moe.kyokobot.bot.JDAEventHandler;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.debug.ModulesCommand;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.impl.CommandManagerImpl;
import moe.kyokobot.bot.util.KyokoJDABuilder;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.requests.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class KyokoService extends AbstractIdleService {
    private final Logger logger;
    private JDA jda;

    private ThreadPoolExecutor executor;
    private ModuleManager moduleManager;
    private DatabaseManager databaseManager;
    private CommandManager commandManager;
    private JDAEventHandler eventHandler;
    private I18n i18n;
    private Settings settings;

    public KyokoService(Settings settings) {
        logger = LoggerFactory.getLogger(getClass());
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        this.settings = settings;

        databaseManager = new DatabaseManager();
        i18n = new I18n(databaseManager);
        commandManager = new CommandManagerImpl(settings, i18n, executor);
        eventHandler = new JDAEventHandler(commandManager);
        moduleManager = new ModuleManager(settings, commandManager);
    }

    @Override
    public void startUp() throws Exception {
        logger.info(" __                 __           __           __                           ");
        logger.info("|  |--.--.--.-----.|  |--.-----.|  |--.-----.|  |_   .--------.-----.-----.");
        logger.info("|    <|  |  |  _  ||    <|  _  ||  _  |  _  ||   _|__|        |  _  |  -__|");
        logger.info("|__|__|___  |_____||__|__|_____||_____|_____||____|__|__|__|__|_____|_____|");
        logger.info("      |_____|                                                              ");

        try {
            KyokoJDABuilder jdaBuilder = new KyokoJDABuilder(AccountType.BOT);

            if (settings.connection.mode.equalsIgnoreCase("gateway")) {
                jdaBuilder.setGateway(settings.connection.gatewayServer);
                Requester.DISCORD_API_PREFIX = settings.connection.restServer;
            }

            jdaBuilder.setAutoReconnect(true);
            jdaBuilder.setToken(settings.connection.token);

            jda = jdaBuilder.buildBlocking();
            jda.addEventListener(eventHandler);

            databaseManager.load(settings);
            i18n.loadMessages();
            //registerCoreCommands();
            moduleManager.loadModules();
        } catch (Exception e) {
            e.printStackTrace();
            this.stopAsync();
        }
    }

    @Override
    public void shutDown() throws Exception {
        if (jda != null) jda.shutdown();
    }

    private void registerCoreCommands() {
        commandManager.registerCommand(new ModulesCommand(moduleManager));
    }
}
