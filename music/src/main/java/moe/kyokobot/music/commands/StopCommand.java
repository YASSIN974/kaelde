package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.music.MusicIcons;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import org.jetbrains.annotations.NotNull;

public class StopCommand extends MusicCommand {
    private final MusicManager musicManager;

    public StopCommand(MusicManager musicManager) {
        name = "stop";
        usage = "";

        this.musicManager = musicManager;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
        if (player.getPlayingTrack() != null) {
            player.stopTrack();
            musicManager.dispose((JDAImpl) context.getEvent().getJDA(), context.getGuild());
            context.send(MusicIcons.STOP + context.getTranslated("music.stopped"));
        } else {
            context.send(CommandIcons.ERROR + context.getTranslated("music.nothingplaying").replace("{shrug}", MusicIcons.SHRUG));
        }
    }
}
