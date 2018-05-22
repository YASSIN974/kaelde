package moe.kyokobot.music.event;

import moe.kyokobot.music.MusicPlayer;

public class PlayerPauseEvent {
    private final MusicPlayer player;

    public PlayerPauseEvent(MusicPlayer player) {
        this.player = player;
    }

    public MusicPlayer getPlayer() {
        return player;
    }
}
