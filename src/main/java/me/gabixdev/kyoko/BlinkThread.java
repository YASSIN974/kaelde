package me.gabixdev.kyoko;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class BlinkThread implements Runnable {
    private final Kyoko kyoko;

    public BlinkThread(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public void run() {
        switch (kyoko.getSettings().getBlinkingShit().toLowerCase()) {
            case "4color":
                kyoko.getLog().info("Blinking shit set to \"4color\".");
                while (kyoko.isRunning()) {
                    try {
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                        Thread.sleep(10000);
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                        kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.STREAMING, kyoko.getSettings().getGame(), "https://twitch.tv/#"));
                        Thread.sleep(10000);
                        kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.DEFAULT, kyoko.getSettings().getGame(), kyoko.getSettings().getGameUrl()));
                        Thread.sleep(10000);
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.IDLE);
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                }
                break;
            case "redyellow":
                kyoko.getLog().info("Blinking shit set to \"redyellow\".");
                kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.DEFAULT, kyoko.getSettings().getGame(), kyoko.getSettings().getGameUrl()));
                while (kyoko.isRunning()) {
                    try {
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                        Thread.sleep(2000);
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.IDLE);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                }
                break;
            case "listening-ry":
                kyoko.getLog().info("Blinking shit set to \"listening-ry\".");
                kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.LISTENING, kyoko.getSettings().getGame(), kyoko.getSettings().getGameUrl()));
                while (kyoko.isRunning()) {
                    try {
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                        Thread.sleep(2000);
                        kyoko.getJda().getPresence().setStatus(OnlineStatus.IDLE);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // nothing
                    }
                }
                break;
            case "listening":
                kyoko.getLog().info("Blinking shit set to \"listening\".");
                kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.LISTENING, kyoko.getSettings().getGame(), kyoko.getSettings().getGameUrl()));
                break;
            case "twitch":
                kyoko.getLog().info("Blinking shit set to \"twitch\".");
                kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.STREAMING, kyoko.getSettings().getGame(), "https://twitch.tv/#"));
                break;
            default:
                kyoko.getLog().info("No blinking shit set.");
        }
    }
}