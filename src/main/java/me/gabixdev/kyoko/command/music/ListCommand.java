package me.gabixdev.kyoko.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.PageUtil;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.List;

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
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
        musicManager.outChannel = message.getTextChannel();

        if (musicManager.scheduler.getQueue().isEmpty()) {
            musicManager.player.stopTrack();
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.empty"), kyoko.getSettings().getPrefix()), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }

        int i = 1;
        int pc = PageUtil.getPageCount(musicManager.scheduler.getQueue(), 10);

        if (args.length == 2) {
            boolean succ = true;
            try {
                i = Integer.parseUnsignedInt(args[1]);

                if (i == 0 || i > pc)
                    succ = false;
            } catch (NumberFormatException e) {
                succ = false;
            }

            if (!succ) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.outrange"), pc), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            }
        }

        StringBuilder list = new StringBuilder();

        AudioTrack currTrack = musicManager.player.getPlayingTrack();
        list.append("**").append(kyoko.getI18n().get(l, "music.msg.currplaying")).append("** ").append(currTrack.getInfo().title).append("\t`[").append(StringUtil.prettyPeriod(currTrack.getPosition())).append("/").append(StringUtil.prettyPeriod(currTrack.getDuration())).append("]`").append("\n\n");

        List<AudioTrack> tli = PageUtil.getPage(musicManager.scheduler.getQueue(), i, 10);
        for (int tid = 0; tid < tli.size(); tid++) {
            list.append("**").append(tid + 1 + ((i - 1) * 10)).append(".** ").append(tli.get(tid).getInfo().title).append("\t`[").append(StringUtil.prettyPeriod(tli.get(tid).getDuration())).append("]`").append("\n");
        }
        list.append("\n").append(String.format(kyoko.getI18n().get(l, "music.msg.skiptip"), kyoko.getSettings().getPrefix()));

        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        err.addField(String.format(kyoko.getI18n().get(l, "music.msg.tracklist"), i, pc), list.toString(), false);
        message.getChannel().sendMessage(err.build()).queue();
        System.gc(); // free memory
    }
}
