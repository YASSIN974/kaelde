package moe.kyokobot.music.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import moe.kyokobot.music.MusicSettings;
import moe.kyokobot.music.event.TrackEndEvent;
import net.dv8tion.jda.core.audio.AudioConnection;
import net.dv8tion.jda.core.audio.AudioWebSocket;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.handle.VoiceServerUpdateHandler;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.managers.impl.AudioManagerImpl;
import net.dv8tion.jda.core.requests.WebSocketClient;
import net.dv8tion.jda.core.utils.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static moe.kyokobot.music.MusicUtil.isChannelEmpty;

public class LocalMusicManager implements MusicManager {
    private final Logger logger;
    private final List<AudioSourceManager> sourceManagers;
    private final Long2ObjectMap<MusicPlayer> players;
    private final Long2ObjectMap<MusicQueue> queues;
    private final Cache<String, AudioItem> resultCache;
    private final AudioPlayerManager playerManager;
    private final EventBus eventBus;

    public LocalMusicManager(MusicSettings settings, EventBus eventBus) {
        logger = LoggerFactory.getLogger(this.getClass());
        sourceManagers = new ArrayList<>();
        players = new Long2ObjectOpenHashMap<>();
        queues = new Long2ObjectOpenHashMap<>();
        resultCache = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(2000).build();
        playerManager = new DefaultAudioPlayerManager();
        this.eventBus = eventBus;
    }


    @Override
    public void registerSourceManager(AudioSourceManager manager) {
        if (manager != null)
            sourceManagers.add(manager);
    }

    @Override
    public AudioItem resolve(Guild guild, String query) {
        AudioItem item = resultCache.getIfPresent(query);
        if (item != null) {
            if (item instanceof AudioTrack)
                return ((AudioTrack) item).makeClone();
            return item;
        }


        for (AudioSourceManager manager : sourceManagers) {
            item = manager.loadItem(null, new AudioReference(query, null));
            if (item != null) {
                resultCache.put(query, item);
                return item;
            }
        }
        return null;
    }

    @Override
    public MusicQueue getQueue(Guild guild) {
        return queues.computeIfAbsent(guild.getIdLong(), queue -> new MusicQueue((JDAImpl) guild.getJDA(), this, guild));
    }

    @Override
    public MusicPlayer getMusicPlayer(Guild guild) {
        return players.computeIfAbsent(guild.getIdLong(), id -> {
            AudioPlayer player = playerManager.createPlayer();
            player.addListener(new LocalEventHandler(eventBus, guild));
            return new LocalPlayerWrapper(player, guild);
        });
    }
    @Override
    public void openConnection(JDAImpl jda, Guild guild, VoiceChannel channel) {
        AudioManagerImpl audioManager = (AudioManagerImpl) guild.getAudioManager();
        audioManager.openAudioConnection(channel);
        audioManager.setSendingHandler(new LocalSendHandler((LocalPlayerWrapper) getMusicPlayer(guild)));
    }

    @Override
    public void closeConnection(JDAImpl jda, Guild guild) {
        AudioManagerImpl audioManager = (AudioManagerImpl) guild.getAudioManager();
        audioManager.closeAudioConnection();
    }

    @Override
    public void clean(JDAImpl jda, Guild guild) {
        guild.getAudioManager().setSendingHandler(null);
        closeConnection(jda, guild);
        MusicPlayer player = players.remove(guild.getIdLong());
        if (player != null)
            player.destroyPlayer();
        queues.remove(guild.getIdLong());
    }

    @Override
    public String getDebug() {
        return "";
    }

    @Override
    public void shutdown() {
        playerManager.shutdown();
    }

    @Override
    public String getDebugString(Guild guild, MusicPlayer player) {
        return "local:s";
    }


    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        clean((JDAImpl) event.getJDA(), event.getGuild());
    }

    @Subscribe
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        JDAImpl jda = (JDAImpl) event.getGuild().getJDA();

        jda.getClient().updateAudioConnection(event.getGuild().getIdLong(), event.getGuild().getSelfMember().getVoiceState().getChannel());

        if (event.getEndpoint() == null)
            return;

        String endpoint = event.getEndpoint().replace(":80", "");

        AudioManagerImpl audioManager = (AudioManagerImpl) event.getGuild().getAudioManager();
        if (audioManager.isConnected())
            audioManager.prepareForRegionChange();
        if (!audioManager.isAttemptingToConnect())
        {
            logger.debug("Received a VOICE_SERVER_UPDATE but JDA is not currently connected nor attempted to connect to a VoiceChannel. Assuming that this is caused by another client running on this account. Ignoring the event.");
            return;
        }

        AudioWebSocket socket = new AudioWebSocket(audioManager.getListenerProxy(), endpoint, jda, event.getGuild(), event.getSessionId(), event.getToken(), audioManager.isAutoReconnect());
        AudioConnection connection = new AudioConnection(socket, audioManager.getQueuedAudioConnection());
        audioManager.setAudioConnection(connection);
        socket.startConnection();
    }

    @Subscribe
    public void onVoiceChannelLeft(GuildVoiceLeaveEvent event) {
        MusicPlayer player = players.get(event.getGuild().getIdLong());
        if (player != null && (event.getChannelLeft().getMembers().contains(event.getGuild().getSelfMember()) && isChannelEmpty(event.getGuild(), event.getChannelLeft())))
            clean((JDAImpl) event.getJDA(), event.getGuild());
    }

    @Subscribe
    public void onTrackEnd(TrackEndEvent event) {
        logger.debug("Track end");
        MusicQueue queue = queues.get(event.getPlayer().getGuildId());
        if (queue != null) {
            if (queue.isRepeating()) {
                event.getPlayer().playTrack(queue.getLastTrack());
            } else {
                Guild g = queue.getJDA().getGuildById(event.getPlayer().getGuildId());

                if (queue.getTracks().isEmpty() && queue.getLastTrack() == null) {
                    clean(queue.getJDA(), g);
                } else if (event.getReason().mayStartNext) {
                    AudioTrack track = queue.poll();

                    if (track == null) {
                        clean(queue.getJDA(), g);
                        return;
                    }

                    event.getPlayer().playTrack(track);
                    queue.announce(track);
                }
            }
        }
    }
}
