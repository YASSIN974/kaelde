package moe.kyokobot.music;

import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.StringUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;

import java.util.Collections;
import java.util.List;

import static java.lang.String.*;
import static moe.kyokobot.music.MusicIcons.PLAY;

public class MusicQueue {
    @Getter
    private final MusicManager manager;

    @Getter
    private final Guild guild;

    private TextChannel announcingChannel;

    private CommandContext context;

    @Getter
    private ObjectLinkedOpenHashSet<AudioTrack> tracks;

    @Getter
    private AudioTrack lastTrack;

    @Getter
    @Setter
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
        return tracks.isEmpty() ? null : (lastTrack = tracks.removeFirst());
    }

    public void announce(AudioTrack track) {
        if (announcingChannel != null) {
            announcingChannel.sendMessage(PLAY + format(context.getTranslated("music.nowplaying"),
                    track.getInfo().title.replace("`", "\\`"),
                    StringUtil.musicPrettyPeriod(track.getDuration()))).queue();
        }
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    public void shuffle() {
        List<AudioTrack> list = Lists.newArrayList(tracks);
        Collections.shuffle(list);
        tracks = new ObjectLinkedOpenHashSet<>(list);
    }

    public void setAnnouncing(TextChannel announcingChannel, CommandContext context) {
        this.announcingChannel = announcingChannel;
        this.context = context;
    }
}
