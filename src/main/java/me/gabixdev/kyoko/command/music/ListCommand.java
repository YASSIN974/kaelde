package me.gabixdev.kyoko.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.LinkedList;
import java.util.List;

import static me.gabixdev.kyoko.util.PageUtil.getPage;
import static me.gabixdev.kyoko.util.PageUtil.getPageCount;

public class ListCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"list"};

    public ListCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "music.list.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        kyoko.run(message.getGuild(), () -> {
            Language l = kyoko.getI18n().getLanguage(message.getMember());

            MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
            musicManager.outChannel = message.getTextChannel();
            AudioTrack currTrack = musicManager.player.getPlayingTrack();
            LinkedList<AudioTrack> queue = musicManager.scheduler.getQueue();

            if (queue.isEmpty()) {
                if(currTrack != null)
                {
                    String builder = "**" + kyoko.getI18n().get(l, "music.msg.currplaying") + "** " + currTrack.getInfo().title + "\t`[" + StringUtil.prettyPeriod(currTrack.getPosition()) + "/" + StringUtil.prettyPeriod(currTrack.getDuration()) + "]`" + "\n\n" +
                            String.format(kyoko.getI18n().get(l, "music.msg.empty"), kyoko.getSettings().getPrefix(), kyoko.getSupportedSources(), kyoko.getSettings().getPrefix(), kyoko.getSettings().getPrefix());
                    EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                    err.addField(kyoko.getI18n().get(l, "music.title"), builder, false);
                    message.getChannel().sendMessage(err.build()).queue();
                    return;
                }
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.empty"), kyoko.getSettings().getPrefix(), kyoko.getSupportedSources(), kyoko.getSettings().getPrefix(), kyoko.getSettings().getPrefix()), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            }

            int i = 1;
            int pc = getPageCount(queue, 10);

            if (args.length == 2) {
                try {
                    i = Integer.parseUnsignedInt(args[1]);
                    if (i == 0 || i > pc)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                    err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.outrange"), pc), false);
                    message.getChannel().sendMessage(err.build()).queue();
                    return;
                }
            }

            StringBuilder list = new StringBuilder();

            list.append("**").append(kyoko.getI18n().get(l, "music.msg.currplaying")).append("** ").append(currTrack.getInfo().title).append("\t`[").append(StringUtil.prettyPeriod(currTrack.getPosition())).append("/").append(StringUtil.prettyPeriod(currTrack.getDuration())).append("]`").append("\n\n");

            List<AudioTrack> tli = getPage(queue, i, 10);
            for (int tid = 0; tid < tli.size(); tid++) {
                list.append("**").append(tid + 1 + ((i - 1) * 10)).append(".** ").append(tli.get(tid).getInfo().title).append("\t`[").append(StringUtil.prettyPeriod(tli.get(tid).getDuration())).append("]`").append("\n");
            }
            list.append("\n").append(String.format(kyoko.getI18n().get(l, "music.msg.skiptip"), kyoko.getSettings().getPrefix()));

            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(String.format(kyoko.getI18n().get(l, "music.msg.tracklist"), i, pc), list.toString(), false);
            message.getChannel().sendMessage(err.build()).queue();
            System.gc(); // free memory
        });
    }
}
