package moe.kyokobot.music;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import io.sentry.Sentry;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.GsonUtil;
import moe.kyokobot.music.commands.PlayCommand;
import moe.kyokobot.music.lavalink.LavaMusicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class Module implements KyokoModule {
    private Logger logger;
    @Inject
    private CommandManager commandManager;
    @Inject
    private Settings settings;
    @Inject
    private EventBus eventBus;
    private ArrayList<Command> commands;
    private MusicManager manager;
    private MusicSettings musicSettings;
    private MusicEventHandler eventHandler = new MusicEventHandler();

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        loadConfig();

        switch (musicSettings.type) {
            case LAVALINK:
                manager = new LavaMusicManager(musicSettings, eventBus);
                break;
        }

        eventBus.register(eventHandler);
        eventBus.register(manager);
        commands.add(new PlayCommand(manager));
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        eventBus.unregister(eventHandler);
        eventBus.unregister(manager);
        commands.forEach(commandManager::unregisterCommand);
    }

    private void loadConfig() {
        File cfg = new File("musicconfig.json");
        if (!cfg.exists()) {
            try {
                if (cfg.createNewFile()) {
                    musicSettings = new MusicSettings();
                    String data = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(musicSettings);
                    Files.write(cfg.toPath(), data.getBytes(Charsets.UTF_8));
                }
            } catch (Exception e) {
                logger.error("Error creating default configuration!");
                e.printStackTrace();
                Sentry.capture(e);
            }
        }

        try {
            musicSettings = GsonUtil.gson.fromJson(new FileReader(cfg), MusicSettings.class);
        } catch (Exception e) {
            logger.error("Cannot read configuration file!");
            e.printStackTrace();
            Sentry.capture(e);
        }
    }
}
