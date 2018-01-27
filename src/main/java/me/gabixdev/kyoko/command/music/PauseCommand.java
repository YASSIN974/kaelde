package me.gabixdev.kyoko.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class PauseCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"pause"};

    public PauseCommand(Kyoko kyoko){
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String getDescription() {
        return "music.pause.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {

        Language language = kyoko.getI18n().getLanguage(message.getGuild());
        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
        musicManager.outChannel = message.getTextChannel();

        AudioTrack audioTrack = musicManager.player.getPlayingTrack();
        if(audioTrack == null) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(language, "music.title"), String.format(kyoko.getI18n().get(language, "music.msg.empty"), kyoko.getSettings().getPrefix()), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }
        if(musicManager.player.isPaused()) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(language, "music.title"), String.format(kyoko.getI18n().get(language, "music.msg.ispaused"), kyoko.getSettings().getPrefix()), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }
        musicManager.player.setPaused(true);
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        err.addField(kyoko.getI18n().get(language, "music.title"), String.format(kyoko.getI18n().get(language, "music.msg.paused"), kyoko.getSettings().getPrefix()), false);
        message.getChannel().sendMessage(err.build()).queue();

    }
}
