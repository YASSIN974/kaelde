package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.VoteUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class PitchCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final DatabaseManager databaseManager;

    public PitchCommand(MusicManager musicManager, DatabaseManager databaseManager) {
        this.musicManager = musicManager;
        this.databaseManager = databaseManager;

        name = "pitch";
        usage = "";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            if (!context.hasArgs()) {
                CommonErrors.usage(context);
                return;
            }
            int f;

            try {
                f = Integer.parseInt(context.getConcatArgs());
            } catch (NumberFormatException e) {
                CommonErrors.notANumber(context, context.getConcatArgs());
                return;
            }

            if (f < -12 || f > 12) {
                context.error(context.getTranslated("music.pitch.outrange"));
                return;
            }

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

            if (player.getPitch() == 0) { // only lock while enabling
                if (VoteUtil.voteLock(context, databaseManager)) {
                    CommonErrors.voteLock(context);
                    return;
                }
            }

            player.setPitch(f);

            if (f == 0) {
                context.info(context.getTranslated("music.pitch.disabled"));
            } else {
                context.info(context.transFormat("music.pitch.set", f));
            }
        }
    }
}
