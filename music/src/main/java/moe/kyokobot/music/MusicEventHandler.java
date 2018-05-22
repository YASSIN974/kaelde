package moe.kyokobot.music;

import com.google.common.eventbus.Subscribe;
import moe.kyokobot.music.event.TrackEndEvent;

public class MusicEventHandler {
    @Subscribe
    public void onTrackEnd(TrackEndEvent event) {

    }
}
