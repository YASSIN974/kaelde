package moe.kyokobot.music.lavalink;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import moe.kyokobot.music.MusicSettings;
import moe.kyokobot.music.event.TrackEndEvent;
import moe.kyokobot.music.event.TrackStartEvent;
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
import java.util.stream.Collectors;

import static moe.kyokobot.music.MusicUtil.isChannelEmpty;

public class LavaMusicManager implements MusicManager {
    private final Logger logger;
    private final LavaClient lavaClient;
    private final LavaEventHandler handler;
    private final List<AudioSourceManager> sourceManagers;
    private final Long2ObjectMap<MusicQueue> queues;
    private final HashMap<AudioNodeEntry, String> nodeNames;

    public LavaMusicManager(MusicSettings settings, EventBus eventBus) {
        logger = LoggerFactory.getLogger(this.getClass());
        sourceManagers = new ArrayList<>();
        queues = new Long2ObjectOpenHashMap<>();
        handler = new LavaEventHandler(eventBus);
        lavaClient = new LavaClientBuilder()
                .setShardCount(Globals.shardCount)
                .setUserId(Globals.clientId).build();
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
        LavaPlayer lavaPlayer = ((LavaPlayerWrapper) getMusicPlayer(guild)).getPlayer();
        return lavaPlayer.getState() != State.CONNECTED ? resolveLocal(query) : resolveRemote(query, lavaPlayer);
    }

    private AudioItem resolveLocal(String query) {
        logger.info("Not connected, resolving locally: {}", query);
        for (AudioSourceManager manager : sourceManagers) {
            AudioItem item = manager.loadItem(null, new AudioReference(query, null));
            if (item != null) {
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
        return queues.computeIfAbsent(guild.getIdLong(), queue -> new MusicQueue(this, guild));
    }

    @Override
    public MusicPlayer getMusicPlayer(Guild guild) {
        LavaPlayer lavaPlayer = lavaClient.newPlayer(guild.getIdLong());
        lavaPlayer.addListener(handler);
        return new LavaPlayerWrapper(lavaPlayer);
    }

    @Override
    public void openConnection(Guild guild, VoiceChannel channel) {
        ((JDAImpl) guild.getJDA()).getClient().queueAudioConnect(channel);
    }

    @Override
    public void closeConnection(Guild guild) {
        ((JDAImpl) guild.getJDA()).getClient().queueAudioDisconnect(guild);
    }

    @Override
    public String getDebug() {
        return "# LavaMusicManager\n\n" +
                "Connected nodes: " + lavaClient.getAudioNodes().size() + "\n" +
                "Active player count: " + lavaClient.getPlayers().size() + "\n";
    }

    @Override
    public void shutdown() {
        lavaClient.shutdown();
    }

    @Override
    public void dispose(Guild guild) {
        closeConnection(guild);
        LavaPlayer lp = lavaClient.getPlayerMap().get(guild.getIdLong());
        if (lp != null && lp.getState() == State.CONNECTED) lp.destroyPlayer();
        queues.remove(guild.getIdLong());
    }

    @Override
    public String getDebugString(Guild guild, MusicPlayer player) {
        String s = guild.getJDA().getShardInfo() == null ? "nil" : String.valueOf(guild.getJDA().getShardInfo().getShardId());
        String n = nodeNames.get((((LavaPlayerWrapper) player).getPlayer()).getConnectedNode().getEntry());
        return s + ":" + (n == null ? "nil" : n) + ":" + guild.getId();
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        dispose(event.getGuild());
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
    public void onVoiceChannelLeave(GuildVoiceLeaveEvent event) {
        LavaPlayer lp = lavaClient.getPlayerMap().get(event.getGuild().getIdLong());
        if (lp != null && (lp.getChannelId() == event.getChannelLeft().getIdLong() && isChannelEmpty(event.getGuild(), event.getChannelLeft())))
            dispose(event.getGuild());
    }

    @Subscribe
    public void onTrackStart(TrackStartEvent event) {
        event.getPlayer().updateFilters(event.getTrack());
    }

    @Subscribe
    public void onTrackEnd(TrackEndEvent event) {
        MusicQueue queue = queues.get(event.getPlayer().getGuildId());
        if (queue != null) {
            if (queue.getRepeating()) {
                event.getPlayer().playTrack(queue.getLastTrack().makeClone());
            } else {
                Guild g = queue.getGuild();

                if (queue.getTracks().isEmpty() && queue.getLastTrack() == null) {
                    dispose(g);
                } else if (event.getReason().mayStartNext) {
                    AudioTrack track = queue.poll();

                    if (track == null) {
                        dispose(g);
                        return;
                    }

                    playAndAnnounce(event, queue, track);
                }
            }
        }
    }

    private void playAndAnnounce(TrackEndEvent event, MusicQueue queue, AudioTrack track) {
        event.getPlayer().playTrack(track);
        queue.announce(track);
    }
}
