package moe.kyokobot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.StringUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import static moe.kyokobot.music.MusicIcons.PLAY;

public class MusicQueue {
    private final MusicManager manager;
    private final Guild guild;
    private TextChannel announcingChannel;
    private CommandContext context;
    private ObjectLinkedOpenHashSet<AudioTrack> tracks;
    private AudioTrack lastTrack;
    private boolean repeating;

    public MusicQueue(MusicManager manager, Guild guild) {
        this.manager = manager;
        this.guild = guild;
        tracks = new ObjectLinkedOpenHashSet<>();
    }

    public void add(AudioTrack track) {
        tracks.add(track);
    }

    public void clear() {
        tracks.clear();
    }

    public AudioTrack poll() {
        return lastTrack = tracks.removeFirst();
    }

    public void announce(AudioTrack track) {
        if (announcingChannel != null) {
            announcingChannel.sendMessage(PLAY + String.format(context.getTranslated("music.nowplaying"), track.getInfo().title.replace("`", "\\`"), StringUtil.musicPrettyPeriod(track.getDuration()))).queue();
        }
    }

    public boolean isEmpty() {
        return tracks.size() == 0;
    }

    public AudioTrack getLastTrack() {
        return lastTrack;
    }

    public ObjectLinkedOpenHashSet<AudioTrack> getTracks() {
        return tracks;
    }

    public void setAnnouncing(TextChannel announcingChannel, CommandContext context) {
        this.announcingChannel = announcingChannel;
        this.context = context;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isRepeating() {
        return repeating;
    }
}
