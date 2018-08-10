package moe.kyokobot.music.commands;

import com.google.common.base.Splitter;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.MessageWaiter;
import moe.kyokobot.music.*;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static moe.kyokobot.music.MusicUtil.locks;

public class SearchCommand extends MusicCommand {

    private final EventWaiter eventWaiter;
    private final MusicManager musicManager;
    private final SearchManager searchManager;

    public SearchCommand(EventWaiter eventWaiter, MusicManager musicManager, SearchManager searchManager) {
        this.eventWaiter = eventWaiter;
        this.musicManager = musicManager;
        this.searchManager = searchManager;

        name = "search";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.hasArgs()) {
            String query = context.getConcatArgs();

            if (idiotTest(query)) {
                context.send(CommandIcons.ERROR + String.format(context.getTranslated("music.nothingfound"), query));
                return;
            }

            SearchManager.SearchResult result = searchManager.searchYouTube(query);
            if (result != null && result.getEntries() != null && !result.getEntries().isEmpty()) {
                EmbedBuilder eb = context.getNormalEmbed();
                eb.setTitle(context.getTranslated("music.searchresults.youtube"));

                StringBuilder sb = new StringBuilder();
                String title;
                for (int i = 0; i < result.getEntries().size(); i++) {
                    title = result.getEntries().get(i).getTitle();
                    sb.append("`").append(i + 1).append(".` ").append(title.length() >= 60 ? title.substring(0, 60) + "..." : title).append("\n");
                }
                sb.append("\n").append(context.getTranslated("music.searchfooter"));
                eb.setDescription(sb.toString());


                context.send(eb.build(), message -> {
                    MessageWaiter mw = new MessageWaiter(eventWaiter, context);
                    mw.setMessageHandler(m -> handleMessage(context, result, m));
                    mw.create();
                });
            } else {
                context.send(CommandIcons.ERROR + String.format(context.getTranslated("music.nothingfound"), query));
            }
        } else {
            CommonErrors.usage(context);
        }
    }

    private void handleMessage(CommandContext context, SearchManager.SearchResult result, MessageReceivedEvent m) {
        {
            if (MusicUtil.lock(context)) return;

            locks.put(context.getGuild(), true);
            String content = m.getMessage().getContentRaw();
            if (content.toLowerCase().contains("exit")) return;

            List<String> splits = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(content);
            int i;
            AudioItem item;
            int items = 0;
            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            MusicQueue queue = musicManager.getQueue(context.getGuild());

            for (String s : splits) {
                try {
                    i = Integer.parseUnsignedInt(s);
                    if (i > 0 && i < result.getEntries().size() + 1) {
                        item = musicManager.resolve(context.getGuild(), result.getEntries().get(i - 1).getUrl());
                        if (item == null) continue;

                        if (item instanceof AudioPlaylist) {
                            for (AudioTrack track : ((AudioPlaylist) item).getTracks()) {
                                queue.add(track);
                                items++;
                            }
                        } else if (item instanceof AudioTrack) {
                            queue.add((AudioTrack) item);
                            items++;
                        } else if (item instanceof AudioReference) {
                            items++;
                        } else {
                            logger.debug("Unknown item type: " + item.getClass().getName());
                        }
                    }
                } catch (NumberFormatException e) {
                    locks.invalidate(context.getGuild());
                    return;
                }
            }

            if (items != 0) {
                VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
                if (voiceChannel != null) {
                    context.send(MusicIcons.PLAY + String.format(context.getTranslated("music.addeditems"), items));
                    ((JDAImpl) context.getEvent().getJDA()).getCallbackPool().submit(() -> {
                        queue.setAnnouncing(context.getChannel(), context);
                        MusicUtil.play(musicManager, player, queue, context, voiceChannel);
                    });
                } else {
                    context.send(CommandIcons.ERROR + context.getTranslated("music.joinchannel"));
                }
            }
            locks.invalidate(context.getGuild());
        }
    }

    private boolean idiotTest(String query) {
        return query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + "play")
                || query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + "p")
                || query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + ">")
                || query.toLowerCase().startsWith(Settings.instance.bot.normalPrefix + "search")
                || query.toLowerCase().startsWith("https://")
                || query.toLowerCase().startsWith("http://");
    }
}
