package moe.kyokobot.music;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class MusicUtil {
    public static VoiceChannel getCurrentChannel(Guild guild, Member member) {
        return guild.getVoiceChannels().stream().filter(voiceChannel -> voiceChannel.getMembers().contains(member)).findFirst().orElse(null);
    }
}
