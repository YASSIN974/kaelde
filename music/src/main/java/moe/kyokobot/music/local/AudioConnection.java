package moe.kyokobot.music.local;

import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public interface AudioConnection {
    void openConnection(Guild guild, VoiceChannel channel, AudioSendHandler handler);
    void closeConnection(Guild guild);
    void onVoiceStateUpdate(VoiceStateUpdateEvent event);
    void onVoiceServerUpdate(VoiceServerUpdateEvent event);
}
