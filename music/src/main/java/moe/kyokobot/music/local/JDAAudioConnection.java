package moe.kyokobot.music.local;

import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.managers.impl.AudioManagerImpl;
import net.dv8tion.jda.core.utils.MiscUtil;

public class JDAAudioConnection implements AudioConnection {

    @Override
    public void openConnection(Guild guild, VoiceChannel channel, AudioSendHandler handler) {
        AudioManagerImpl audioManager = (AudioManagerImpl) guild.getAudioManager();
        audioManager.openAudioConnection(channel);
        if (handler != null)
            audioManager.setSendingHandler(handler);
    }

    @Override
    public void closeConnection(Guild guild) {
        ((JDAImpl) guild.getJDA()).getCallbackPool().submit(() -> {
            AudioManagerImpl audioManager = (AudioManagerImpl) guild.getAudioManager();
            audioManager.closeAudioConnection();
        });
    }

    @Override
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        // no need to do anything
    }

    @Override
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        JDAImpl jda = (JDAImpl) event.getGuild().getJDA();

        jda.getClient().updateAudioConnection(event.getGuild().getIdLong(), event.getGuild().getSelfMember().getVoiceState().getChannel());

        if (event.getEndpoint() == null)
            return;

        String endpoint = event.getEndpoint().replace(":80", "");

        AudioManagerImpl audioManager = (AudioManagerImpl) event.getGuild().getAudioManager();
        MiscUtil.locked(audioManager.CONNECTION_LOCK, () -> {
            if (audioManager.isConnected())
                audioManager.prepareForRegionChange();
            if (!audioManager.isAttemptingToConnect()) {
                return;
            }

            net.dv8tion.jda.core.audio.AudioConnection connection = new net.dv8tion.jda.core.audio.AudioConnection(audioManager, endpoint, event.getSessionId(), event.getToken());
            audioManager.setAudioConnection(connection);
            connection.startConnection();
        });
    }
}
