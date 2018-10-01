package moe.kyokobot.music.local;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import moe.kyokobot.music.MusicSettings;
import moe.kyokobot.music.event.TrackEndEvent;
import moe.kyokobot.music.event.TrackStartEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static moe.kyokobot.music.MusicUtil.isChannelEmpty;

public class LocalMusicManager implements MusicManager {
    private final Logger logger;
    private final List<AudioSourceManager> sourceManagers;
    private final Long2ObjectMap<MusicPlayer> players;
    private final Long2ObjectMap<MusicQueue> queues;
    private final AudioConnection connectionHandler;
    private final AudioPlayerManager playerManager;
    private final EventBus eventBus;

    public LocalMusicManager(MusicSettings settings, EventBus eventBus) {
        logger = LoggerFactory.getLogger(this.getClass());
        sourceManagers = new ArrayList<>();
        players = new Long2ObjectOpenHashMap<>();
        queues = new Long2ObjectOpenHashMap<>();

        connectionHandler = new JDAAudioConnection();

        playerManager = new DefaultAudioPlayerManager();
        playerManager.setFrameBufferDuration(600);
        playerManager.getConfiguration().setFilterHotSwapEnabled(true);
        playerManager.getConfiguration().setOpusEncodingQuality(10);
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        LocalPlayerWrapper.configuration = playerManager.getConfiguration();
        this.eventBus = eventBus;
    }


    @Override
    public void registerSourceManager(AudioSourceManager manager) {
        if (manager != null)
            sourceManagers.add(manager);
    }

    @Override
    public AudioItem resolve(Guild guild, String query) {
        AudioItem item;
        for (AudioSourceManager manager : sourceManagers) {
            item = manager.loadItem(null, new AudioReference(query, null));
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Override
    public MusicQueue getQueue(Guild guild) {
        return queues.computeIfAbsent(guild.getIdLong(), queue -> new MusicQueue(this, guild));
    }

    @Override
    public MusicPlayer getMusicPlayer(Guild guild) {
        return players.computeIfAbsent(guild.getIdLong(), id -> {
            AudioPlayer player = playerManager.createPlayer();

            MusicPlayer wrapper = new LocalPlayerWrapper(player, guild);

            player.addListener(new LocalEventHandler(wrapper, eventBus));

            return wrapper;
        });
    }

    @Override
    public void openConnection(Guild guild, VoiceChannel channel) {
        if (connectionHandler != null)
            connectionHandler.openConnection(guild, channel, new LocalSendHandler((LocalPlayerWrapper) getMusicPlayer(guild)));
    }

    @Override
    public void closeConnection(Guild guild) {
        if (connectionHandler != null)
            connectionHandler.closeConnection(guild);
    }

    @Override
    public void dispose(Guild guild) {
        closeConnection(guild);
        MusicPlayer player = players.remove(guild.getIdLong());
        if (player != null)
            player.destroyPlayer();
        queues.remove(guild.getIdLong());
    }

    @Override
    public String getDebug() {
        StringBuilder sb = new StringBuilder();

        sb.append("# LocalMusicManager\n\n");

        sb.append("Connection handler: ").append(connectionHandler.getClass().getName()).append("\n");
        sb.append("Audio connections: ").append(players.size()).append("\n");

        return sb.toString();
    }

    @Override
    public void shutdown() {
        playerManager.shutdown();
    }

    @Override
    public String getDebugString(Guild guild, MusicPlayer player) {
        return "local:s" + guild.getJDA().getShardInfo().getShardId();
    }

    @Subscribe
    public void onChannelRemove(VoiceChannelDeleteEvent event) {
        if (event.getChannel().getIdLong() == getMusicPlayer(event.getGuild()).getChannelId()) {
            dispose(event.getGuild());
        }
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        dispose(event.getGuild());
    }

    @Subscribe
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        if (connectionHandler != null)
            connectionHandler.onVoiceStateUpdate(event);
    }

    @Subscribe
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        if (connectionHandler != null)
            connectionHandler.onVoiceServerUpdate(event);
    }

    @Subscribe
    public void onVoiceChannelLeave(GuildVoiceLeaveEvent event) {
        MusicPlayer player = players.get(event.getGuild().getIdLong());
        Guild guild = event.getGuild();
        List<Member> members = event.getChannelLeft().getMembers();

        if (player != null && (members.contains(guild.getSelfMember()) && isChannelEmpty(guild, event.getChannelLeft())))
            dispose(guild);
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
