package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.music.MusicUtil;
import me.gabixdev.kyoko.music.SearchResult;
import me.gabixdev.kyoko.music.YoutubeSearch;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import me.gabixdev.kyoko.util.exception.APIException;
import me.gabixdev.kyoko.util.exception.NotFoundException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class PlayYoutubeCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"playyoutube", "ytplay", "playyt"};

    public PlayYoutubeCommand(Kyoko kyoko) {
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
        return "music.playyoutube.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public String getUsage() {
        return "music.playsearch.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        VoiceChannel vc = MusicUtil.getCurrentMemberChannel(message.getGuild(), message.getMember());
        if (vc == null) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.plsjoin"), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }

        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
        musicManager.outChannel = message.getTextChannel();

        if (args.length == 1) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        String[] mp = new String[args.length - 1];
        System.arraycopy(args, 1, mp, 0, args.length - 1);
        String search = String.join(" ", mp);
        String url = "";

        try {
            SearchResult sr = YoutubeSearch.search(search);
            if (sr.getEntries().isEmpty()) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.notfound"), search), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            } else {
                url = sr.getEntries().get(0).getURL();
            }
        } catch (NotFoundException ex) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "music.msg.notfound"), search), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        } catch (Exception ex) {
            kyoko.getLog().severe(ex.getMessage());
            ex.printStackTrace();

            if (ex instanceof APIException) {
                kyoko.getLog().severe("Data: " + ((APIException) ex).getRaw());
            }
            message.getTextChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "generic.error.message"), Constants.DISCORD_URL), false).build()).queue();
            message.getTextChannel().sendMessage(Constants.DISCORD_URL).queue();
            return;
        }

        for (VoiceChannel voiceChannel : message.getGuild().getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(message.getMember())) {
                try {
                    message.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                    MusicUtil.loadAndPlay(kyoko, l, musicManager, url, true);
                } catch (PermissionException e) {
                    CommonErrorUtil.noPermissionBot(kyoko, l, message.getTextChannel());
                }
                return;
            }
        }
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.plsjoin"), false);
        message.getChannel().sendMessage(err.build()).queue();
    }
}
