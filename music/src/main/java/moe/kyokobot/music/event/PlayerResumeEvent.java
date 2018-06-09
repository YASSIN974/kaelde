package moe.kyokobot.music.event;

import moe.kyokobot.music.MusicPlayer;

public class PlayerResumeEvent {
    private final MusicPlayer player;

    public PlayerResumeEvent(MusicPlayer player) {
        this.player = player;
    }

    public MusicPlayer getPlayer() {
        return player;
    }
}

