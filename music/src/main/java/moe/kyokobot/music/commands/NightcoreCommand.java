package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class NightcoreCommand extends MusicCommand {

    private final MusicManager musicManager;

    public NightcoreCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        name = "nightcore";
        //experimental = true;
    }

    @Override
    public void execute(CommandContext context) {
        VoiceChannel voiceChannel = context.getMember().getVoiceState().getChannel();
        if (voiceChannel != null) {
            float f;

            try {
                f = Float.parseFloat(context.getConcatArgs());
            } catch (NumberFormatException e) {
                CommonErrors.notANumber(context, context.getConcatArgs());
                return;
            }

            if (f < 0.5f || f > 2.0f) {
                context.send(CommandIcons.ERROR + context.getTranslated("music.nightcore.outrange"));
                return;
            }

            MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
            player.setNightcore(f);

            if (f == 1.0f) {
                context.send(CommandIcons.INFO + context.getTranslated("music.nightcore.disabled"));
            } else {
                context.send(CommandIcons.INFO + String.format(context.getTranslated("music.nightcore.enabled"), f));
            }
        }
    }
}
