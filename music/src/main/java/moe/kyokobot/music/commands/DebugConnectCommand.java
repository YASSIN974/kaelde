package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.music.MusicManager;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.JDAImpl;

public class DebugConnectCommand extends Command {

    private final MusicManager musicManager;

    public DebugConnectCommand(MusicManager musicManager) {
        name = "debugconnect";
        type = CommandType.DEBUG;

        this.musicManager = musicManager;
    }

    @Override
    public void execute(CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();

        if (voiceChannel != null)
            musicManager.openConnection((JDAImpl) context.getEvent().getJDA(), context.getGuild(), voiceChannel);
    }
}
