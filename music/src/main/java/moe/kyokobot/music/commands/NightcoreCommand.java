package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.VoteUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class NightcoreCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final DatabaseManager databaseManager;

    public NightcoreCommand(MusicManager musicManager, DatabaseManager databaseManager) {
        this.musicManager = musicManager;
        this.databaseManager = databaseManager;

        name = "nightcore";
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
            float f;

            try {
                f = Float.parseFloat(context.getConcatArgs());
            } catch (NumberFormatException e) {
                CommonErrors.notANumber(context, context.getConcatArgs());
                return;
            }

            if (f < 0.1f || f > 3.0f) {
                context.send(CommandIcons.ERROR + context.getTranslated("music.nightcore.outrange"));
                return;
            }

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

            if (player.getNightcore() == 1.0f) { // only lock while enabling
                if (VoteUtil.voteLock(context, databaseManager)) {
                    CommonErrors.voteLock(context);
                    return;
                }
            }

            player.setNightcore(f);

            if (f == 1.0f) {
                context.send(CommandIcons.INFO + context.getTranslated("music.nightcore.disabled"));
            } else {
                context.send(CommandIcons.INFO + String.format(context.getTranslated("music.nightcore.enabled"), f));
            }
        }
    }
}
