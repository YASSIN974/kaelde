package moe.kyokobot.bot.event;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;

@Getter
public class VoiceServerUpdateEvent {
    private final String token;
    private final Guild guild;
    private final String endpoint;
    private final String sessionId;

    public VoiceServerUpdateEvent(String token, Guild guild, String endpoint, String sessionId) {
        this.token = token;
        this.guild = guild;
        this.endpoint = endpoint;
        this.sessionId = sessionId;
    }
}
