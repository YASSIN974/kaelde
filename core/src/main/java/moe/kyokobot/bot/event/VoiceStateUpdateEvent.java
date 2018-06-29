package moe.kyokobot.bot.event;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;

@Getter
public class VoiceStateUpdateEvent {
    public VoiceStateUpdateEvent(Guild guild, Long channelId, String userId, String sessionId, boolean deaf, boolean mute, boolean selfDeaf, boolean selfMute, boolean suppress) {
        this.guild = guild;
        this.channelId = channelId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.deaf = deaf;
        this.mute = mute;
        this.selfDeaf = selfDeaf;
        this.selfMute = selfMute;
        this.suppress = suppress;
    }

    private Guild guild;
    private Long channelId;
    private String userId;
    private String sessionId;
    private boolean deaf;
    private boolean mute;
    private boolean selfDeaf;
    private boolean selfMute;
    private boolean suppress;
}
