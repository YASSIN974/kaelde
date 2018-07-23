package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.VoteUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class VaporwaveCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final DatabaseManager databaseManager;

    public VaporwaveCommand(MusicManager musicManager, DatabaseManager databaseManager) {
        this.musicManager = musicManager;
        this.databaseManager = databaseManager;

        name = "vaporwave";
    }

    @Override
    public void execute(CommandContext context) {
        if (VoteUtil.voteLock(context, databaseManager)) {
            CommonErrors.voteLock(context);
            return;
        }

        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            player.setVaporwave(!player.isVaporwave());
            context.send(CommandIcons.INFO + context.getTranslated("music.vaporwave." + (player.isVaporwave() ? "enabled" : "disabled")));
        }
    }
}
