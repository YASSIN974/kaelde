package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jsoup.helper.StringUtil;

import javax.annotation.Nonnull;

import static java.lang.Integer.parseInt;
import static moe.kyokobot.bot.command.CommandIcons.ERROR;
import static moe.kyokobot.bot.util.StringUtil.markdown;

public class RemoveCommand extends MusicCommand {

    private final MusicManager musicManager;
    private static final int BASE_TEN = 10;

    public RemoveCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        checkChannel = true;
        name = "remove";
    }

    @Override
    public void execute(@Nonnull CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            if (context.hasArgs()) {
                String query = context.getConcatArgs().trim();
                TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
                if (channel == null)
                    channel = context.getChannel();
                if (StringUtil.isNumeric(query.toLowerCase())) {
                    int index = parseInt(query, BASE_TEN) - 1;
                    if ((index < 0) || (index > queue.getTracks().size())) {
                        context.error(context.transFormat("music.invalidindex", query));
                    } else {
                        channel.sendMessage( MusicIcons.REMOVE + context.transFormat("music.removed", markdown(queue.getTracks().get(index).getAudioTrack().getInfo().title))).queue();
                        queue.remove(index);
                    }
                } else if (query.toLowerCase().equals("~")) {
                    queue.removeDuplicates();
                    channel.sendMessage(MusicIcons.REMOVE + context.getTranslated("music.remove.duplicates")).queue();
                } else if (query.matches("<@([0-9])+>")) {
                    String trimmedQuery = query.substring(2, query.length() - 1);
                    queue.removeUser(trimmedQuery);
                    channel.sendMessage(MusicIcons.REMOVE + context.transFormat("music.remove.user", trimmedQuery)).queue();
                } else {
                    CommonErrors.usage(context);
                }
            } else
                CommonErrors.usage(context);
        } else
            context.error(context.getTranslated("music.joinchannel"));
    }
}
