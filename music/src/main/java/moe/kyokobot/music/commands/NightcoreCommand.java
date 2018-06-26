package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class NightcoreCommand extends MusicCommand {

    private final MusicManager musicManager;

    public NightcoreCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
        name = "nightcore";
    }

    @Override
    public void execute(CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            // TODO check that user is in same channel as Kyoko.

            float f;
            try {
                f = Float.parseFloat(context.getConcatArgs());
            } catch (NumberFormatException e) {
                context.send(CommandIcons.ERROR + "not a float number");
                return;
            }

            if (f < 0.5f || f > 2.0f) {
                context.send(CommandIcons.ERROR + "Out of range (0.5-2.0)!");
                return;
            }

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            player.setNightcore(f);
            context.send(CommandIcons.INFO + "nightcore set to " + f);
        }
    }
}
