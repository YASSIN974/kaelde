package moe.kyokobot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.Paginator;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends MusicCommand {
    private final MusicManager musicManager;
    private final EventWaiter waiter;

    public ListCommand(MusicManager musicManager, EventWaiter waiter) {
        this.musicManager = musicManager;
        this.waiter = waiter;

        name = "list";
        aliases = new String[] {"queue", "q"};
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        MusicQueue queue = musicManager.getQueue(context.getGuild());

        List<String> pages = StringUtil.createRawPages(queue.getTracks().stream().map(track -> track.getInfo().title.length() > 60 ? track.getInfo().title.substring(0, 60) + "..." : track.getInfo().title).collect(Collectors.toList()));
        EmbedBuilder eb = context.getNormalEmbed();

        Paginator paginator = new Paginator(waiter, pages, context.getSender()) {
            private AudioTrack prev;

            @Override
            protected Message render(int page) {
                MessageBuilder mb = new MessageBuilder();
                StringBuilder sb = new StringBuilder();

                if (player.getPlayingTrack() != null) {
                    if (prev != player.getPlayingTrack()) {
                        prev = player.getPlayingTrack();
                        pageContents = StringUtil.createRawPages(queue.getTracks().stream().map(track -> track.getInfo().title.length() > 60 ? track.getInfo().title.substring(0, 60) + "..." : track.getInfo().title).collect(Collectors.toList()));
                    }
                    String title = player.getPlayingTrack().getInfo().title.replace("`", "\\`");
                    sb.append("Now playing: `").append(title.length() > 80 ? title.substring(0, 80) + "..." : title)
                            .append("` `[").append(StringUtil.musicPrettyPeriod(player.getPlayingTrack().getDuration())).append("]`\n\n");
                }

                if (page < 0) page = 0; else if (page >= pageContents.size()) page = pageContents.size() - 1;
                eb.setTitle(MusicIcons.MUSIC + context.getTranslated("music.list.title") + (pageContents.isEmpty() ? "" : (" (" + (page + 1) + "/" + pageContents.size() + ")")));

                if (pageContents.isEmpty())
                    sb.append(context.getTranslated("music.queueempty").replace("{prefix}", context.getPrefix()));
                else {
                    sb.append("```markdown\n");
                    sb.append(pageContents.get(page));
                    sb.append("\n```");
                }

                eb.setDescription(sb.toString());
                mb.setEmbed(eb.build());
                return mb.build();
            }
        };
        TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
        if (channel == null)
            channel = context.getChannel();
        paginator.create(channel);
    }
}
