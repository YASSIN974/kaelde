package moe.kyokobot.music.local;

import com.google.common.eventbus.EventBus;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import moe.kyokobot.music.MusicPlayer;

public class LocalEventHandler extends AudioEventAdapter {

    private final MusicPlayer musicPlayer;
    private final EventBus eventBus;

    public LocalEventHandler(MusicPlayer musicPlayer, EventBus eventBus) {
        this.musicPlayer = musicPlayer;
        this.eventBus = eventBus;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (musicPlayer instanceof LocalPlayerWrapper)
            ((LocalPlayerWrapper) musicPlayer).setPlayer(player);
        eventBus.post(new moe.kyokobot.music.event.TrackStartEvent(musicPlayer, track));
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        if (musicPlayer instanceof LocalPlayerWrapper)
            ((LocalPlayerWrapper) musicPlayer).setPlayer(player);
        eventBus.post(new moe.kyokobot.music.event.TrackStuckEvent(musicPlayer, track, thresholdMs));
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (musicPlayer instanceof LocalPlayerWrapper)
            ((LocalPlayerWrapper) musicPlayer).setPlayer(player);
        eventBus.post(new moe.kyokobot.music.event.TrackEndEvent(musicPlayer, track, endReason));
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        if (musicPlayer instanceof LocalPlayerWrapper)
            ((LocalPlayerWrapper) musicPlayer).setPlayer(player);
        eventBus.post(new moe.kyokobot.music.event.TrackExceptionEvent(musicPlayer, track, exception));
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if (musicPlayer instanceof LocalPlayerWrapper)
            ((LocalPlayerWrapper) musicPlayer).setPlayer(player);
        eventBus.post(new moe.kyokobot.music.event.PlayerPauseEvent(musicPlayer));
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if (musicPlayer instanceof LocalPlayerWrapper)
            ((LocalPlayerWrapper) musicPlayer).setPlayer(player);
        eventBus.post(new moe.kyokobot.music.event.PlayerResumeEvent(musicPlayer));
    }
}
