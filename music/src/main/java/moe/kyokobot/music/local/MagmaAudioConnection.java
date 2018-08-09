package moe.kyokobot.music.local;

import moe.kyokobot.bot.event.VoiceServerUpdateEvent;
import moe.kyokobot.bot.event.VoiceStateUpdateEvent;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class MagmaAudioConnection implements AudioConnection {
    //private final MagmaApi api = MagmaApi.of(m -> new NativeAudioSendFactory());
    //private final Long2ObjectMap<Member> memberMap = new Long2ObjectOpenHashMap<>();

    @Override
    public void openConnection(Guild guild, VoiceChannel channel, AudioSendHandler handler) {
        /*((JDAImpl) guild.getJDA()).pool.submit(() -> {
            ((JDAImpl) guild.getJDA()).getClient().queueAudioConnect(channel);
            api.setSendHandler(getSelfMember(guild), handler);
        });*/
    }

    @Override
    public void closeConnection(Guild guild) {
        /*((JDAImpl) guild.getJDA()).pool.submit(() -> {
            api.removeSendHandler(getSelfMember(guild));
            api.closeConnection(getSelfMember(guild));
            cleanSelf(guild);
            ((JDAImpl) guild.getJDA()).getClient().queueAudioDisconnect(guild);
        });*/
    }

    @Override
    public void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        // unused
    }

    @Override
    public void onVoiceServerUpdate(VoiceServerUpdateEvent event) {
        /*MagmaServerUpdate serverUpdate = MagmaServerUpdate.builder()
                .sessionId(event.getSessionId())
                .endpoint(event.getEndpoint())
                .token(event.getToken())
                .build();

        api.provideVoiceServerUpdate(getSelfMember(event.getGuild()), serverUpdate);*/
    }

    /*private Member getSelfMember(Guild guild) {
        return memberMap.computeIfAbsent(guild.getIdLong(), g ->
                MagmaMember.builder()
                        .userId(guild.getSelfMember().getUser().getId())
                        .guildId(guild.getId())
                        .build());
    }

    private void cleanSelf(Guild guild) {
        memberMap.remove(guild.getIdLong());
    }*/
}
