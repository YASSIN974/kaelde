package moe.kyokobot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.music.MusicIcons.STOP;

public class SkipCommand extends MusicCommand {
    private final MusicManager musicManager;

    public SkipCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        name = "skip";
        description = "music.skip.description";
        aliases = new String[] {">>", "forceskip"};
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        // TODO voteskip

        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            // TODO check that user is in same channel as Kyoko.

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            MusicQueue queue = musicManager.getQueue(context.getGuild());

            if (context.hasArgs()) {
                try {
                    int i = Integer.parseUnsignedInt(context.getConcatArgs()) - 1;
                    if (i < 0) i = 0;

                    while (i != 0 && !queue.isEmpty()) {
                        queue.poll();
                        i--;
                    }
                } catch (NumberFormatException e) {
                    CommonErrors.notANumber(context, context.getConcatArgs());
                    return;
                }
            }

            if (queue.isEmpty()) {

                if (player.getPlayingTrack() != null) {
                    context.send(STOP + context.getTranslated("music.stopped"));

                    musicManager.dispose(context.getGuild());
                } else {
                    context.send(CommandIcons.ERROR + context.getTranslated("music.queueempty").replace("{prefix}", context.getPrefix()));

                    musicManager.dispose(context.getGuild());
                }

            } else {
                boolean wasRepeating = queue.isRepeating();

                // TODO: enable/disable announcing
                queue.setAnnouncing(context.getChannel(), context);

                AudioTrack track = queue.poll();

                if (wasRepeating)
                    queue.setRepeating(false);

                player.playTrack(track);

                if (wasRepeating)
                    queue.setRepeating(true);

                queue.announce(track);
            }
        } else
            context.send(CommandIcons.ERROR + context.getTranslated("music.joinchannel"));
    }
}