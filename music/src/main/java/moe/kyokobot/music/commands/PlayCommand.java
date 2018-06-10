package moe.kyokobot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicPlayer;
import moe.kyokobot.music.MusicQueue;
import moe.kyokobot.music.MusicUtil;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static moe.kyokobot.music.MusicIcons.PLAY;

public class PlayCommand extends MusicCommand {
    private final MusicManager musicManager;

    public PlayCommand(MusicManager musicManager) {
        this.musicManager = musicManager;

        name = "play";
        description = "music.play.description";
        aliases = new String[] {">", "p"};
    }

    @Override
    public void execute(CommandContext context) {
        if (context.hasArgs()) {
            VoiceChannel voiceChannel = MusicUtil.getCurrentChannel(context.getGuild(), context.getMember());
            if (voiceChannel != null) {
                MusicPlayer player = musicManager.getMusicPlayer(context.getGuild());
                MusicQueue queue = musicManager.getQueue(context.getGuild());
                AudioItem item = musicManager.resolve(context.getConcatArgs().trim());

                // TODO: enable/disable announcing
                queue.setAnnouncing(context.getChannel(), context);

                if (item == null) {
                    context.send(CommandIcons.error + String.format(context.getTranslated("music.nothingfound"), context.getConcatArgs()));
                } else {
                    if (item instanceof AudioPlaylist) {
                        int tracks = 0;
                        for (AudioTrack track : ((AudioPlaylist) item).getTracks()) {
                            queue.add(track);
                            tracks++;
                        }
                        context.send(PLAY + String.format(context.getTranslated("music.addedplaylist"), tracks, ((AudioPlaylist) item).getName().replace("`", "\\`")));
                    } else if (item instanceof AudioTrack) {
                        queue.add((AudioTrack) item);
                        context.send(PLAY + String.format(context.getTranslated("music.added"), ((AudioTrack) item).getInfo().title.replace("`", "\\`")));
                    }

                    if (player.getPlayingTrack() == null) {
                        musicManager.openConnection(context.getGuild(), voiceChannel);
                        player.playTrack(queue.poll());
                    }
                }
            } else {
                context.send(CommandIcons.error + context.getTranslated("music.joinchannel"));
            }
        } else {
            CommonErrors.usage(context);
        }
    }

    @SubCommand
    public void debug(CommandContext context) {
        context.send("```\n" + musicManager.getDebug() + "\n```");
    }
}
