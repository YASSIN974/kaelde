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
        if (!context.hasArgs()) {
            CommonErrors.usage(context);
            return;
        }

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
                    if (index < 0 || index > queue.getTracks().size()) {
                        channel.sendMessage(ERROR + "\"" + query + "\" is an invalid index.").queue();
                    } else {
                        queue.remove(index);
                        channel.sendMessage( MusicIcons.REMOVE + "Removed " + query + "from queue.").queue();
                    }
                } else {
                    queue.removeUser(query);
                    channel.sendMessage(MusicIcons.REMOVE + "Removed " + query + "'s tracks from queue.").queue();
                }
            }
        } else
            context.error(context.getTranslated("music.joinchannel"));
    }
}
