package moe.kyokobot.music.lavalink;

import com.google.common.eventbus.Subscribe;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import samophis.lavalink.client.entities.LavaClient;
import samophis.lavalink.client.entities.builders.AudioNodeEntryBuilder;
import samophis.lavalink.client.entities.builders.LavaClientBuilder;

public class LavaMusicManager implements MusicManager {
    private LavaClient lavaClient;
    private MusicSettings settings;

    public LavaMusicManager(MusicSettings settings) {
        this.settings = settings;

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
        });
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {

    }

    @Override
    public MusicPlayer getMusicPlayer(Guild guild) {
        return null;
    }
}
