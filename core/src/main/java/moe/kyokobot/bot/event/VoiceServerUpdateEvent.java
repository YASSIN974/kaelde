package moe.kyokobot.bot.event;

import net.dv8tion.jda.core.entities.Guild;

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

    public String getToken() {
        return token;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getSessionId() {
        return sessionId;
    }
}
