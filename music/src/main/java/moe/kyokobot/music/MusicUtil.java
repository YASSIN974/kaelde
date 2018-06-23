package moe.kyokobot.music;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class MusicUtil {
    public static boolean isChannelEmpty(Guild guild, VoiceChannel channel) {
        if (guild == null || channel == null) return true;
        return channel.getMembers().stream().allMatch(member -> member.getUser().isBot());
    }
}
