package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicUtil;
import net.dv8tion.jda.core.entities.VoiceChannel;
import samophis.lavalink.client.entities.LavaPlayer;

public class PlayCommand extends Command {
    private final MusicManager manager;

    public PlayCommand(MusicManager manager) {
        this.manager = manager;

        name = "play";
        description = "play.description";
        aliases = new String[] {">", "p"};
        category = CommandCategory.MUSIC;
    }

    @Override
    public void execute(CommandContext context) {
        VoiceChannel voiceChannel = MusicUtil.getCurrentChannel(context.getGuild(), context.getMember());
        if (voiceChannel != null) {
            manager.openConnection(context.getGuild(), voiceChannel);
            manager.getMusicPlayer(context.getGuild()).playTrack(context.getConcatArgs());
        } else {
            context.send(context.error() + "Join voice channel first!");
        }
    }
}
