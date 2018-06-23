package moe.kyokobot.music.local;

import com.google.common.eventbus.EventBus;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;

public class LocalEventHandler extends AudioEventAdapter {

    private final EventBus eventBus;
    private final Guild guild;

    public LocalEventHandler(EventBus eventBus, Guild guild) {
        this.eventBus = eventBus;
        this.guild = guild;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        eventBus.post(new moe.kyokobot.music.event.TrackStartEvent(new LocalPlayerWrapper(player, guild), track));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        eventBus.post(new moe.kyokobot.music.event.TrackStuckEvent(new LocalPlayerWrapper(player, guild), track, thresholdMs));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        eventBus.post(new moe.kyokobot.music.event.TrackEndEvent(new LocalPlayerWrapper(player, guild), track, endReason));
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        eventBus.post(new moe.kyokobot.music.event.TrackExceptionEvent(new LocalPlayerWrapper(player, guild), track, exception));
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        eventBus.post(new moe.kyokobot.music.event.PlayerPauseEvent(new LocalPlayerWrapper(player, guild)));
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        eventBus.post(new moe.kyokobot.music.event.PlayerResumeEvent(new LocalPlayerWrapper(player, guild)));
    }
}
