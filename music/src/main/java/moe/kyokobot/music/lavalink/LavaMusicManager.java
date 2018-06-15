package moe.kyokobot.music.lavalink;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import moe.kyokobot.music.MusicSettings;
import moe.kyokobot.music.event.TrackEndEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.VoiceState;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;
import samophis.lavalink.client.entities.State;
import samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder;
import samophis.lavalink.client.entities.builders.LavaClientBuilder;
import samophis.lavalink.client.entities.internal.LavaPlayerImpl;

import java.util.ArrayList;
import java.util.List;

import static moe.kyokobot.music.MusicUtil.isChannelEmpty;

public class LavaMusicManager implements MusicManager {
    private final Logger logger;
    private final LavaClient lavaClient;
    private final LavaEventHandler handler;
    private final List<AudioSourceManager> sourceManagers;
    private final Long2ObjectOpenHashMap<MusicQueue> queues;
    //private final Long2ObjectOpenHashMap<EventWaiter> waiters;

    public LavaMusicManager(MusicSettings settings, EventBus eventBus) {
        logger = LoggerFactory.getLogger(this.getClass());
        sourceManagers = new ArrayList<>();
        queues = new Long2ObjectOpenHashMap<>();
        //waiters = new Long2ObjectOpenHashMap<>();
        handler = new LavaEventHandler(eventBus);
        lavaClient = new LavaClientBuilder(true)
                .setShardCount(Globals.shardCount)
                .setUserId(Globals.clientId).build();
        settings.nodes.forEach(node -> {
            AudioNodeEntryBuilder builder = new AudioNodeEntryBuilder(lavaClient);
            builder.setAddress(node.host);
            if (node.password != null && !node.password.isEmpty()) {
                builder.setPassword(node.password);
            }
            builder.setWebSocketPort(node.wsPort);
            builder.setRestPort(node.restPort);
            lavaClient.addEntry(builder.build());
        });
    }

    @Override
    public void registerSourceManager(AudioSourceManager manager) {
        if (manager != null)
            sourceManagers.add(manager);
    }

    @Override
    public AudioItem resolve(String query) {
        for (AudioSourceManager manager : sourceManagers) {
            AudioItem item = manager.loadItem(null, new AudioReference(query, null));
            if (item != null) return item;
        }
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

        //EventWaiter waiter = getWaiter(guild.getIdLong());
        //waiter.tryConnect();
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
        //sb.append("Waiters: ").append(waiters.size()).append("\n");
        sb.append("Active player count: ").append(lavaClient.getPlayers().size()).append("\n");
        return sb.toString();
    }

    @Override
    public void shutdown() {
        System.out.println("Shutdown");
        lavaClient.shutdown();
    }

    @Override
    public void clean(JDAImpl jda, Guild guild) {
        closeConnection(jda, guild);
        LavaPlayer lp = lavaClient.getPlayerMap().get(guild.getIdLong());
        if (lp != null && lp.getState() == State.CONNECTED) lp.destroyPlayer();
        //waiters.remove(guild.getIdLong());
        queues.remove(guild.getIdLong());
    }

    /* private EventWaiter getWaiter(long id) {
        return waiters.computeIfAbsent(id, waiter -> EventWaiter.from(lavaClient, id));
    }*/

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        clean((JDAImpl) event.getJDA(), event.getGuild());
    }

    @Subscribe
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        //EventWaiter waiter = getWaiter(event.getGuild().getIdLong());
        //waiter.setServerAndTryConnect(event.getToken(), event.getEndpoint());
        VoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
        if (voiceState != null)
            lavaClient.newPlayer(event.getGuild().getIdLong()).connect(voiceState.getSessionId(), event.getToken(), event.getEndpoint());
        else logger.warn("VoiceState == null?");
    }

    @Subscribe
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        //EventWaiter waiter = getWaiter(event.getGuild().getIdLong());
        //waiter.setSessionIdAndTryConnect(event.getSessionId());
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
        if (lp != null) {
            if (lp.getChannelId() == event.getChannelLeft().getIdLong() && isChannelEmpty(event.getGuild(), event.getChannelLeft())) {
                clean((JDAImpl) event.getJDA(), event.getGuild());
            }
        }
    }

    @Subscribe
    public void onTrackEnd(TrackEndEvent event) {
        MusicQueue queue = queues.get(event.getPlayer().getGuildId());
        if (queue != null) {
            if (queue.isRepeating()) {
                event.getPlayer().playTrack(queue.getLastTrack());
            } else {
                Guild g = queue.getJDA().getGuildById(event.getPlayer().getGuildId());
                if (queue.getTracks().size() == 0 && queue.getLastTrack() == null) {
                    clean(queue.getJDA(), g);
                } else {
                    if (event.getReason().mayStartNext) {
                        AudioTrack track = queue.poll();
                        if (track == null) {
                            clean(queue.getJDA(), g);
                        } else {
                            event.getPlayer().playTrack(track);
                            queue.announce(track);
                        }
                    }
                }
            }
        }
    }
}
