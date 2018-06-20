package moe.kyokobot.music.lavalink;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import moe.kyokobot.music.*;
import moe.kyokobot.music.event.TrackEndEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.VoiceState;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.*;
import samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder;
import samophis.lavalink.client.entities.builders.LavaClientBuilder;
import samophis.lavalink.client.entities.internal.LavaPlayerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static moe.kyokobot.music.MusicUtil.isChannelEmpty;

public class LavaMusicManager implements MusicManager {
    private final Logger logger;
    private final LavaClient lavaClient;
    private final LavaEventHandler handler;
    private final List<AudioSourceManager> sourceManagers;
    private final Long2ObjectOpenHashMap<MusicQueue> queues;
    private final HashMap<AudioNodeEntry, String> nodeNames;
    private Cache<String, AudioItem> resultCache;

    public LavaMusicManager(MusicSettings settings, EventBus eventBus) {
        logger = LoggerFactory.getLogger(this.getClass());
        sourceManagers = new ArrayList<>();
        queues = new Long2ObjectOpenHashMap<>();
        handler = new LavaEventHandler(eventBus);
        lavaClient = new LavaClientBuilder(true)
                .setShardCount(Globals.shardCount)
                .setUserId(Globals.clientId).build();
        resultCache = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(2000).build();
        nodeNames = new HashMap<>();
        settings.nodes.forEach(node -> {
            AudioNodeEntryBuilder builder = new AudioNodeEntryBuilder(lavaClient);
            builder.setAddress(node.host);
            if (node.password != null && !node.password.isEmpty()) {
                builder.setPassword(node.password);
            }
            builder.setWebSocketPort(node.wsPort);
            builder.setRestPort(node.restPort);
            AudioNodeEntry n = builder.build();
            lavaClient.addEntry(n);
        });
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
            logger.info("Using cached resolve result for: {}", query);
            return item;
        }

        LavaPlayer lavaPlayer = ((LavaPlayerWrapper) getMusicPlayer(guild)).getPlayer();
        return lavaPlayer.getState() != State.CONNECTED ? resolveLocal(query) : resolveRemote(query, lavaPlayer);
    }

    private AudioItem resolveLocal(String query) {
        logger.info("Not connected, resolving locally: {}", query);
        for (AudioSourceManager manager : sourceManagers) {
            AudioItem item = manager.loadItem(null, new AudioReference(query, null));
            if (item != null) {
                resultCache.put(query, item);
                return item;
            }
        }
        return null;
    }

    private AudioItem resolveRemote(String query, LavaPlayer lavaPlayer) {
        logger.info("Connected, resolving remotely: {}", query);
        AudioWrapper wrapper = lavaPlayer.loadTracks(query);
        if (wrapper.isPlaylist()) {
            List<AudioTrack> tracks = wrapper.getLoadedTracks().stream().map(TrackDataPair::getTrack).collect(Collectors.toList());
            return new BasicAudioPlaylist(wrapper.getPlaylistName(), tracks, wrapper.getSelectedTrack() != null ? wrapper.getSelectedTrack().getTrack() : null, false);
        } else if (!wrapper.getLoadedTracks().isEmpty())
            return wrapper.getLoadedTracks().get(0).getTrack();
        return null;
    }

    @Override
    public MusicQueue getQueue(Guild guild) {
        return queues.computeIfAbsent(guild.getIdLong(), queue -> new MusicQueue((JDAImpl) guild.getJDA(), this, guild));
    }

    @Override
    public MusicPlayer getMusicPlayer(Guild guild) {
        LavaPlayer lavaPlayer = lavaClient.newPlayer(guild.getIdLong());
        lavaPlayer.addListener(handler);
        return new LavaPlayerWrapper(lavaPlayer);
    }

    @Override
    public void openConnection(JDAImpl jda, Guild guild, VoiceChannel channel) {
        jda.getClient().queueAudioConnect(channel);
    }

    @Override
    public void closeConnection(JDAImpl jda, Guild guild) {
        jda.getClient().queueAudioDisconnect(guild);
    }

    @Override
    public String getDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("LavaMusicManager\n");
        sb.append("----------------\n");
        sb.append("Connected nodes: ").append(lavaClient.getAudioNodes().size()).append("\n");
        sb.append("Active player count: ").append(lavaClient.getPlayers().size()).append("\n");
        return sb.toString();
    }

    @Override
    public void shutdown() {
        lavaClient.shutdown();
    }

    @Override
    public void clean(JDAImpl jda, Guild guild) {
        closeConnection(jda, guild);
        LavaPlayer lp = lavaClient.getPlayerMap().get(guild.getIdLong());
        if (lp != null && lp.getState() == State.CONNECTED) lp.destroyPlayer();
        queues.remove(guild.getIdLong());
    }

    @Override
    public String getDebugString(Guild guild, MusicPlayer player) {
        String s = guild.getJDA().getShardInfo() == null ? "nil" : String.valueOf(guild.getJDA().getShardInfo().getShardId());
        String n = nodeNames.get(((LavaPlayer) player).getConnectedNode().getEntry());
        return s + ":" + (n == null ? "nil" : n) + ":" + guild.getId();
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        clean((JDAImpl) event.getJDA(), event.getGuild());
    }

    @Subscribe
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        VoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
        if (voiceState != null)
            lavaClient.newPlayer(event.getGuild().getIdLong()).connect(voiceState.getSessionId(), event.getToken(), event.getEndpoint());
        else logger.warn("VoiceState == null?");
    }

    @Subscribe
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        LavaPlayer lavaPlayer = lavaClient.newPlayer(event.getGuild().getIdLong());
        if (event.getChannelId() != null) {
            ((LavaPlayerImpl) lavaPlayer).setChannelId(event.getChannelId());
        } else {
            if (lavaPlayer.getState() == State.CONNECTED)
                lavaPlayer.destroyPlayer();
        }
    }

    @Subscribe
    public void onVoiceChannelLeft(GuildVoiceLeaveEvent event) {
        LavaPlayer lp = lavaClient.getPlayerMap().get(event.getGuild().getIdLong());
        if (lp != null && (lp.getChannelId() == event.getChannelLeft().getIdLong() && isChannelEmpty(event.getGuild(), event.getChannelLeft())))
            clean((JDAImpl) event.getJDA(), event.getGuild());
    }

    @Subscribe
    public void onTrackEnd(TrackEndEvent event) {
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
