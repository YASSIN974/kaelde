package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.VoteUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class KaraokeCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final DatabaseManager databaseManager;

    public KaraokeCommand(MusicManager musicManager, DatabaseManager databaseManager) {
        this.musicManager = musicManager;
        this.databaseManager = databaseManager;
        checkChannel = true;
        name = "karaoke";
        usage = "";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            // TODO check that user is in same channel as Kyoko.

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());

            if (!player.isKaraoke()) { // only lock while enabling
                if (VoteUtil.voteLock(context, databaseManager)) {
                    CommonErrors.voteLock(context);
                    return;
                }
            }

            player.setKaraoke(!player.isKaraoke());
            MusicQueue queue = musicManager.getQueue(context.getGuild());
            TextChannel channel = queue.getBoundChannel() == null ? queue.getAnnouncingChannel() : queue.getBoundChannel();
            if (channel == null)
                channel = context.getChannel();
            channel.sendMessage(CommandIcons.INFO + context.getTranslated("music.karaoke." + (player.isVaporwave() ? "enabled" : "disabled"))).queue();
        }
    }

    @SubCommand
    public void config(CommandContext context) {

    }
}
