package moe.kyokobot.music;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import io.sentry.Sentry;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.ModuleManager;
import moe.kyokobot.bot.module.KyokoModule;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.GsonUtil;
import moe.kyokobot.music.commands.*;
import moe.kyokobot.music.lavalink.LavaMusicManager;
import moe.kyokobot.music.local.LocalMusicManager;
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
    private ModuleManager moduleManager;
    @Inject
    private CommandManager commandManager;
    @Inject
    private Settings settings;
    @Inject
    private EventBus eventBus;
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
        if (!settings.apiKeys.containsKey("youtube")) {
            logger.warn("No YouTube API key found, disabling the module!");
            moduleManager.stopModule("music");
            return;
        }

        loadConfig();

        switch (musicSettings.type) {
            case LAVALINK:
                logger.info("Using Lavalink music manager.");
                musicManager = new LavaMusicManager(musicSettings, eventBus);
                break;
            case INTERNAL:
                logger.info("Using internal music manager.");
                musicManager = new LocalMusicManager(musicSettings, eventBus);
                break;
        }

        if (Globals.inKyokoServer) { // Kyoko Discord Bot Support
            MusicIcons.PLAY = "<:play:435575362722856970>  |  ";
            MusicIcons.MUSIC = "<:music:435576097497808927>  |  ";
            MusicIcons.REPEAT = "<:repeat:452127280597303306>  |  ";
            MusicIcons.STOP = "<:stop:435574600076754944>  |  ";
        }

        musicManager.registerSourceManager(new YoutubeAudioSourceManager());
        musicManager.registerSourceManager(new SoundCloudAudioSourceManager());
        musicManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        musicManager.registerSourceManager(new VimeoAudioSourceManager());
        musicManager.registerSourceManager(new BandcampAudioSourceManager());
        musicManager.registerSourceManager(new BeamAudioSourceManager());
        musicManager.registerSourceManager(new LocalAudioSourceManager());
        musicManager.registerSourceManager(new HttpAudioSourceManager());

        eventBus.register(musicManager);

        SearchManager searchManager = new SearchManager(settings.apiKeys.get("youtube"));

        commands = new ArrayList<>();

        commands.add(new PlayCommand(musicManager, searchManager));
        commands.add(new ListCommand(musicManager, waiter));
        commands.add(new SkipCommand(musicManager));
        commands.add(new RepeatCommand(musicManager));
        commands.add(new NightcoreCommand(musicManager));
        commands.add(new PauseCommand(musicManager));

        commands.forEach(commandManager::registerCommand);
    }

    @Override
    public void shutDown() {
        if (musicManager != null) {
            musicManager.shutdown();
            eventBus.unregister(musicManager);
        }

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
