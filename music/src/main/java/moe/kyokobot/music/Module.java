package moe.kyokobot.music;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import io.sentry.Sentry;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.GsonUtil;
import moe.kyokobot.music.commands.ListCommand;
import moe.kyokobot.music.commands.PlayCommand;
import moe.kyokobot.music.lavalink.LavaMusicManager;
import net.dv8tion.jda.core.JDA;
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
    @Inject
    private JDA jda;
    @Inject
    private EventWaiter waiter;
    private ArrayList<Command> commands;
    private MusicManager musicManager;
    private MusicSettings musicSettings;

    public Module() {
        logger = LoggerFactory.getLogger(getClass());
        commands = new ArrayList<>();
    }

    @Override
    public void startUp() {
        loadConfig();

        switch (musicSettings.type) {
            case LAVALINK:
                musicManager = new LavaMusicManager(musicSettings, eventBus, jda);
                break;
        }

        if (jda.getGuildById("375752406727786498") != null) { // Kyoko Discord Bot Support
            MusicIcons.PLAY = "<:play:435575362722856970>  |  ";
        }

        musicManager.registerSourceManager(new YoutubeAudioSourceManager());

        eventBus.register(musicManager);
        commands.add(new PlayCommand(musicManager));
        commands.add(new ListCommand(musicManager, waiter));
        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        musicManager.shutdown();
        eventBus.unregister(musicManager);
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
