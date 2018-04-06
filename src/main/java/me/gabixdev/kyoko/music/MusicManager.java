package me.gabixdev.kyoko.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import me.gabixdev.kyoko.Kyoko;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class MusicManager {
    private final Kyoko kyoko;
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    public TextChannel outChannel;
    public final Guild guild;
    public AudioPlayerSendHandler sendHandler;

    public MusicManager(AudioPlayerManager manager, Guild guild, Kyoko kyoko) {
        this.kyoko = kyoko;
        this.guild = guild;
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, kyoko, this);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        if (sendHandler == null) sendHandler = new AudioPlayerSendHandler(kyoko, player);

        return sendHandler;
    }
}
