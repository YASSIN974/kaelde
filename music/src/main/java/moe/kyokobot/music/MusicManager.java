package moe.kyokobot.music;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public interface MusicManager {
    MusicPlayer getMusicPlayer(Guild guild);
    void openConnection(Guild guild, VoiceChannel channel);
    void closeConnection(Guild guild);
}
