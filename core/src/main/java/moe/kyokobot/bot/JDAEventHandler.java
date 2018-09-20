package moe.kyokobot.bot;

import com.google.common.eventbus.EventBus;
import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.handle.SocketHandler;
import net.dv8tion.jda.core.hooks.EventListener;
import org.json.JSONObject;

import java.util.Map;

public class JDAEventHandler implements EventListener {
    private EventBus eventBus;

    JDAEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onEvent(Event event) {
        if (Globals.eventsSeen != Long.MAX_VALUE) // prevent integer overflow
            Globals.eventsSeen++;

        if (event instanceof ReadyEvent) {
            Map<String, SocketHandler> handlers = ((JDAImpl) event.getJDA()).getClient().getHandlers(); // for lavalink

            //handlers.put("PRESENCE_UPDATE", new PresenceUpdateHandler((JDAImpl) event.getJDA()));
            handlers.put("VOICE_SERVER_UPDATE", new VoiceServerUpdateHandler((JDAImpl) event.getJDA()));
            handlers.put("VOICE_STATE_UPDATE", new VoiceStateUpdateHandler((JDAImpl) event.getJDA()));
        } else if (event instanceof MessageReceivedEvent) {
            if (!((MessageReceivedEvent) event).getAuthor().isBot())
                eventBus.post(event);
        } else {
            eventBus.post(event);
        }
    }

    private class VoiceServerUpdateHandler extends net.dv8tion.jda.core.handle.VoiceServerUpdateHandler {
        VoiceServerUpdateHandler(JDAImpl jda) {
            super(jda);
        }

        @Override
        protected Long handleInternally(JSONObject content) {
            String token = content.getString("token");
            String id = content.getString("guild_id");
            String endpoint = content.getString("endpoint");
            Guild guild = getJDA().getGuildMap().get(Long.valueOf(id));
            String sessionId = guild.getSelfMember().getVoiceState().getSessionId();
            eventBus.post(new VoiceServerUpdateEvent(token, guild, endpoint, sessionId));
            return null;
        }
    }

    private class VoiceStateUpdateHandler extends net.dv8tion.jda.core.handle.VoiceStateUpdateHandler {
        VoiceStateUpdateHandler(JDAImpl jda) {
            super(jda);
        }

        @Override
        protected Long handleInternally(JSONObject content) {
            Long channelId = null;
            if (!content.isNull("channel_id"))
                channelId = Long.valueOf(content.getString("channel_id"));
            String userId = content.getString("user_id");
            String sessionId = content.getString("session_id");
            boolean deaf = content.getBoolean("deaf");
            boolean mute = content.getBoolean("mute");
            boolean selfDeaf = content.getBoolean("self_deaf");
            boolean selfMute = content.getBoolean("self_mute");
            boolean suppress = content.getBoolean("suppress");
            String guildId = content.getString("guild_id");
            long guildIdLong = Long.valueOf(guildId);
            Guild guild = getJDA().getGuildMap().get(guildIdLong);

            if (userId.equals(getJDA().getSelfUser().getId())) {
                eventBus.post(new VoiceStateUpdateEvent(guild, channelId, userId, sessionId, deaf, mute, selfDeaf, selfMute, suppress));
            }
            getJDA().getClient().updateAudioConnection(guildIdLong, guild.getVoiceChannelById(channelId == null ? -1 : channelId));
            return super.handleInternally(content);
        }
    }
}
