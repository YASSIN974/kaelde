package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.VoteUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class VaporwaveCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final DatabaseManager databaseManager;

    public VaporwaveCommand(MusicManager musicManager, DatabaseManager databaseManager) {
        this.musicManager = musicManager;
        this.databaseManager = databaseManager;

        name = "vaporwave";
        checkChannel = true;
    }

    @Override
    public void execute(CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

            if (!player.isVaporwave()) { // only lock while enabling
                if (VoteUtil.voteLock(context, databaseManager)) {
                    CommonErrors.voteLock(context);
                    return;
                }
            }

            player.setVaporwave(!player.isVaporwave());
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
            if (channel == null)
                channel = context.getChannel();
            channel.sendMessage(CommandIcons.INFO + context.getTranslated("music.vaporwave." + (player.isVaporwave() ? "enabled" : "disabled")));
        }
    }
}
