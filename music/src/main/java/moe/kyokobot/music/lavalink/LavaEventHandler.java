package moe.kyokobot.music.lavalink;

import com.google.common.eventbus.EventBus;
import moe.kyokobot.music.MusicPlayer;
import samophis.lavalink.client.entities.events.*;

public class LavaEventHandler implements AudioEventListener {
    private final EventBus eventBus;

    public LavaEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onTrackStart(TrackStartEvent event) {
        eventBus.post(new moe.kyokobot.music.event.TrackStartEvent(new MusicPlayerWrapper(event.getPlayer()), event.getTrack()));
    }

    @Override
    public void onTrackStuck(TrackStuckEvent event) {
        eventBus.post(new moe.kyokobot.music.event.TrackStuckEvent(new MusicPlayerWrapper(event.getPlayer()), event.getTrack(), event.getThresholdMs()));
    }

    @Override
    public void onTrackEnd(TrackEndEvent event) {
        eventBus.post(new moe.kyokobot.music.event.TrackEndEvent(new MusicPlayerWrapper(event.getPlayer()), event.getTrack(), event.getReason()));
    }

    @Override
    public void onTrackException(TrackExceptionEvent event) {
        eventBus.post(new moe.kyokobot.music.event.TrackExceptionEvent(new MusicPlayerWrapper(event.getPlayer()), event.getTrack(), event.getException()));
    }

    @Override
    public void onPlayerPause(PlayerPauseEvent event) {
        eventBus.post(new moe.kyokobot.music.event.PlayerPauseEvent(new MusicPlayerWrapper(event.getPlayer())));
    }

    @Override
    public void onPlayerResume(PlayerResumeEvent event) {
        eventBus.post(new moe.kyokobot.music.event.PlayerResumeEvent(new MusicPlayerWrapper(event.getPlayer())));
    }
}
