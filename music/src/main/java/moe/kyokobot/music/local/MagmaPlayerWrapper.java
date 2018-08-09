package moe.kyokobot.music.local;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.entities.Guild;

public class MagmaPlayerWrapper extends LocalPlayerWrapper {
    public volatile boolean connected = false;

    public MagmaPlayerWrapper(AudioPlayer player, Guild guild) {
        super(player, guild);
    }

    // workaround for magma
    @Override
    public boolean isConnected() {
        return connected;
    }
}
