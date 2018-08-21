package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import net.dv8tion.jda.core.entities.VoiceChannel;

import javax.annotation.Nonnull;

public class RemoveCommand extends MusicCommand {

    private final MusicManager musicManager;

    public RemoveCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        usage = "music.skip.usage";
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
        } else
            context.error(context.getTranslated("music.joinchannel"));
    }
}
