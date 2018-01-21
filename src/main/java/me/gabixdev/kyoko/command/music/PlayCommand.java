package me.gabixdev.kyoko.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;

public class PlayCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"play"};

    public PlayCommand(Kyoko kyoko) {
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
        return "music.play.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public String getUsage() {
        return "music.play.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        if (args.length == 1) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        String[] mp = new String[args.length - 1];
        System.arraycopy(args, 1, mp, 0, args.length - 1);
        String url = String.join(" ", mp);

        for (VoiceChannel voiceChannel : message.getGuild().getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(message.getMember())) {
                message.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
                kyoko.getPlayerManager().loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                        err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.added"), track.getInfo().title), false);
                        message.getChannel().sendMessage(err.build()).queue();

                        musicManager.scheduler.queue(track);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        AudioTrack firstTrack = playlist.getSelectedTrack();
                        if (firstTrack == null) {
                            firstTrack = playlist.getTracks().get(0);
                        }

                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                        err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.playlistadded"), firstTrack.getInfo().title, playlist.getName(), playlist.getTracks().size()), false);
                        message.getChannel().sendMessage(err.build()).queue();

                        musicManager.scheduler.queue(firstTrack);
                    }

                    @Override
                    public void noMatches() {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.notfound"), url), false);
                        message.getChannel().sendMessage(err.build()).queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.error"), exception.getMessage()), false);
                        message.getChannel().sendMessage(err.build()).queue();
                    }
                });


                return;
            }
        }
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.plsjoin"), false);
        message.getChannel().sendMessage(err.build()).queue();
    }
}
