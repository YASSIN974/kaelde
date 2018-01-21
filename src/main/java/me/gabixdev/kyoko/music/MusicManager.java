package me.gabixdev.kyoko.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.gabixdev.kyoko.Kyoko;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class MusicManager {
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    public TextChannel outChannel;
    public final Guild guild;

    public MusicManager(AudioPlayerManager manager, Guild guild, Kyoko kyoko) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, kyoko, this);
        player.addListener(scheduler);
        this.guild = guild;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}
