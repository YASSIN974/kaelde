package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class KaraokeCommand extends MusicCommand {

    private final MusicManager musicManager;

    public KaraokeCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        name = "karaoke";
        usage = "";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            // TODO check that user is in same channel as Kyoko.

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            player.setKaraoke(!player.isKaraoke());
            context.send(CommandIcons.INFO + "karaoke enabled: " + player.isKaraoke());
        }
    }

    @SubCommand
    public void config(CommandContext context) {

    }
}
