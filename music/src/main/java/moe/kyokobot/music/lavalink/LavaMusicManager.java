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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import samophis.lavalink.client.entities.EventWaiter;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;
import samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder;
import samophis.lavalink.client.entities.builders.LavaClientBuilder;
import samophis.lavalink.client.entities.internal.LavaPlayerImpl;

import java.util.ArrayList;
import java.util.List;

import static moe.kyokobot.music.MusicUtil.isChannelEmpty;

public class LavaMusicManager implements MusicManager {
    private final LavaClient lavaClient;
    private final LavaEventHandler handler;
    private final JDA jda;
    private final List<AudioSourceManager> sourceManagers;
    private final Long2ObjectOpenHashMap<MusicQueue> queues;
    private final Long2ObjectOpenHashMap<EventWaiter> waiters;

    public LavaMusicManager(MusicSettings settings, EventBus eventBus, JDA jda) {
        this.jda = jda;
        sourceManagers = new ArrayList<>();
        queues = new Long2ObjectOpenHashMap<>();
        waiters = new Long2ObjectOpenHashMap<>();
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
            builder.setWsPort(node.wsPort);
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
        return queues.computeIfAbsent(guild.getIdLong(), queue -> new MusicQueue(this, guild));
    }

    @Override
    public MusicPlayer getMusicPlayer(Guild guild) {
        LavaPlayer lavaPlayer = lavaClient.getPlayerByGuildId(guild.getIdLong());
        lavaPlayer.addListener(handler);
        return new MusicPlayerWrapper(lavaPlayer);
    }

    @Override
    public void openConnection(Guild guild, VoiceChannel channel) {
        JDAImpl jda = (JDAImpl) guild.getJDA();
        jda.getClient().queueAudioConnect(channel);
        EventWaiter waiter = getWaiter(guild.getIdLong());
        waiter.tryConnect();
    }

    @Override
    public void closeConnection(Guild guild) {
        JDAImpl jda = (JDAImpl) guild.getJDA();
        jda.getClient().queueAudioDisconnect(guild);
    }

    @Override
    public String getDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("LavaMusicManager\n");
        sb.append("----------------\n");
        sb.append("Connected nodes: ").append(lavaClient.getAudioNodes().size()).append("\n");
        sb.append("Waiters: ").append(waiters.size()).append("\n");
        sb.append("Active player count: ").append(lavaClient.getPlayers().size()).append("\n");
        return sb.toString();
    }

    @Override
    public void shutdown() {
        lavaClient.getPlayers().forEach((lavaPlayer) -> {
            Guild g = jda.getGuildById(lavaPlayer.getGuildId());
            if (g != null) ((JDAImpl) jda).getClient().queueAudioDisconnect(g);
            //lavaPlayer.destroyPlayer();
        });
        lavaClient.getAudioNodes().forEach(node -> node.getSocket().sendClose());
    }

    @Override
    public void clean(Guild guild) {
        closeConnection(guild);
        LavaPlayer lp = lavaClient.getPlayerMap().get(guild.getIdLong());
        if (lp != null) lp.destroyPlayer();
        waiters.remove(guild.getIdLong());
        queues.remove(guild.getIdLong());
    }

    private EventWaiter getWaiter(long id) {
        return waiters.computeIfAbsent(id, waiter -> EventWaiter.from(id));
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        clean(event.getGuild());
    }

    @Subscribe
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        EventWaiter waiter = getWaiter(event.getGuild().getIdLong());
        waiter.setServerAndTryConnect(event.getToken(), event.getEndpoint());
    }

    @Subscribe
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        EventWaiter waiter = getWaiter(event.getGuild().getIdLong());
        waiter.setSessionIdAndTryConnect(event.getSessionId());
    }

    @Subscribe
    public void onVoiceChannelLeft(GuildVoiceLeaveEvent event) {
        LavaPlayer lp = lavaClient.getPlayerMap().get(event.getGuild().getIdLong());
        if (lp != null) {
            for (VoiceChannel vc : event.getGuild().getVoiceChannels()) {
                if (vc.getMembers().contains(event.getGuild().getSelfMember())) {
                    ((LavaPlayerImpl) lp).setChannelId(vc.getIdLong());
                    break;
                }
            }

            if (lp.getChannelId() == event.getChannelLeft().getIdLong() && isChannelEmpty(event.getGuild(), event.getChannelLeft())) {
                clean(event.getGuild());
            } else {
                System.out.println("channel is not empty");
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
                Guild g = jda.getGuildById(event.getPlayer().getGuildId());
                if (queue.getTracks().size() == 0 && queue.getLastTrack() == null) {
                    clean(g);
                } else {
                    if (event.getReason().mayStartNext) {
                        AudioTrack track = queue.poll();
                        if (track == null) {
                            clean(g);
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
