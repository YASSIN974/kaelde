package moe.kyokobot.music;

import net.dv8tion.jda.core.entities.Guild;

public interface MusicManager {
    MusicPlayer getMusicPlayer(Guild guild);
}
