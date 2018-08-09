package moe.kyokobot.music;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public interface MusicManager {
    void registerSourceManager(AudioSourceManager manager);
    AudioItem resolve(Guild guild, String query);
    MusicQueue getQueue(Guild guild);
    MusicPlayer getMusicPlayer(Guild guild);
    void openConnection(Guild guild, VoiceChannel channel);
    void closeConnection(Guild guild);
    void dispose(Guild guild);
    String getDebug();
    void shutdown();
    String getDebugString(Guild guild, MusicPlayer player);
}
