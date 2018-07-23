package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.VoteUtil;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class VolumeCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final DatabaseManager databaseManager;

    public VolumeCommand(MusicManager musicManager, DatabaseManager databaseManager) {
        this.musicManager = musicManager;
        this.databaseManager = databaseManager;

        name = "volume";
        aliases = new String[] {"vol"};
    }

    @Override
    public void execute(CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            if (!context.hasArgs()) {
                CommonErrors.usage(context);
                return;
            }
            int f;

            try {
                f = Integer.parseUnsignedInt(context.getConcatArgs());
            } catch (NumberFormatException e) {
                CommonErrors.notANumber(context, context.getConcatArgs());
                return;
            }

            if (f <= 0 || f > 500) {
                context.send(CommandIcons.ERROR + context.getTranslated("music.volume.outrange"));
                return;
            }

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

            if (player.getVolume() == 100) { // only lock while enabling
                if (VoteUtil.voteLock(context, databaseManager)) {
                    CommonErrors.voteLock(context);
                    return;
                }
            }

            player.setVolume(f);


            context.send(MusicIcons.VOLUME + String.format(context.getTranslated("music.volume.set"), f));
        }
    }
}
