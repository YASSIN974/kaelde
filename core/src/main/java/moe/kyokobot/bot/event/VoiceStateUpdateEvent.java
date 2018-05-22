package moe.kyokobot.bot.event;

import net.dv8tion.jda.core.entities.Guild;

public class VoiceStateUpdateEvent {
    public VoiceStateUpdateEvent(Guild guild, long channel_id, String user_id, String session_id, boolean deaf, boolean mute, boolean self_deaf, boolean self_mute, boolean suppress) {
        this.guild = guild;
        this.channel_id = channel_id;
        this.user_id = user_id;
        this.session_id = session_id;
        this.deaf = deaf;
        this.mute = mute;
        this.self_deaf = self_deaf;
        this.self_mute = self_mute;
        this.suppress = suppress;
    }

    private Guild guild;
    private long channel_id;
    private String user_id;
    private String session_id;
    private boolean deaf;
    private boolean mute;
    private boolean self_deaf;
    private boolean self_mute;
    private boolean suppress;

    public Guild getGuild() {
        return guild;
    }

    public long getChannelId() {
        return channel_id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getSessionId() {
        return session_id;
    }

    public boolean isDeaf() {
        return deaf;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isSelfDeaf() {
        return self_deaf;
    }

    public boolean isSelfMute() {
        return self_mute;
    }

    public boolean isSuppress() {
        return suppress;
    }
}
