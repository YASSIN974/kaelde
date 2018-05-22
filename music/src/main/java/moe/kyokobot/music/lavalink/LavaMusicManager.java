package moe.kyokobot.music.lavalink;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import samophis.lavalink.client.entities.EventWaiter;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.LavaPlayer;
import samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder;
import samophis.lavalink.client.entities.builders.LavaClientBuilder;
import samophis.lavalink.client.entities.internal.LavaPlayerImpl;

import java.util.HashMap;

public class LavaMusicManager implements MusicManager {
    private final LavaClient lavaClient;
    private final MusicSettings settings;
    private final EventBus eventBus;
    private LavaEventHandler handler;
    private HashMap<Long, EventWaiter> waiters;

    public LavaMusicManager(MusicSettings settings, EventBus eventBus) {
        this.settings = settings;
        this.eventBus = eventBus;

        waiters = new HashMap<>();
        handler = new LavaEventHandler(eventBus);

        lavaClient = new LavaClientBuilder(true)
                .setShardCount(Globals.shardCount)
                .setUserId(Globals.clientId).build();

        settings.nodes.forEach(node -> {
            //System.out.println(node);
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
    public MusicPlayer getMusicPlayer(Guild guild) {
        return lavaClient.getRawPlayers().computeIfAbsent(guild.getIdLong(), player -> {
            LavaPlayer lp = new LavaPlayerImpl(lavaClient, guild.getIdLong());
            lp.addListener(handler);
            return lp;
        });
    }

    @Override
    public void openConnection(Guild guild, VoiceChannel channel) {
        JDAImpl jda = (JDAImpl) guild.getJDA();
        jda.getClient().queueAudioConnect(channel);
        //guild.getAudioManager().openAudioConnection(channel);
        EventWaiter waiter = getWaiter(guild.getIdLong());
        waiter.tryConnect();
    }

    @Override
    public void closeConnection(Guild guild) {
        JDAImpl jda = (JDAImpl) guild.getJDA();
        jda.getClient().queueAudioDisconnect(guild);
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        closeConnection(event.getGuild());
        waiters.remove(event.getGuild().getIdLong());
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

    private EventWaiter getWaiter(long id) {
        return waiters.computeIfAbsent(id, waiter -> EventWaiter.from(id));
    }
}
