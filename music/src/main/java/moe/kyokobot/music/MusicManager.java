package moe.kyokobot.music;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;

public interface MusicManager {
    void registerSourceManager(AudioSourceManager manager);
    AudioItem resolve(String query);
    MusicQueue getQueue(Guild guild);
    MusicPlayer getMusicPlayer(Guild guild);
    void openConnection(JDAImpl jda, Guild guild, VoiceChannel channel);
    void closeConnection(JDAImpl jda, Guild guild);
    void clean(JDAImpl jda, Guild guild);
    String getDebug();
    void shutdown();
}
