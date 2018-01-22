package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.music.MusicUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

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

        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
        musicManager.outChannel = message.getTextChannel();

        if (args.length == 1) {
            if (musicManager.player.isPaused()) {
                musicManager.player.setPaused(false);

                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.resumed"), false);
                message.getChannel().sendMessage(err.build()).queue();
            } else if (musicManager.player.getPlayingTrack() != null) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.alreadyplaying"), false);
                message.getChannel().sendMessage(err.build()).queue();
            } else if (musicManager.scheduler.getQueue().isEmpty()) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.empty"), kyoko.getSettings().getPrefix()), false);
                message.getChannel().sendMessage(err.build()).queue();
            }
            //printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        String[] mp = new String[args.length - 1];
        System.arraycopy(args, 1, mp, 0, args.length - 1);
        String url = String.join(" ", mp);

        for (VoiceChannel voiceChannel : message.getGuild().getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(message.getMember())) {
                try {
                    message.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                    MusicUtil.loadAndPlay(kyoko, l, musicManager, url, true);
                } catch (PermissionException e) {
                    if (e.getPermission() == Permission.VOICE_CONNECT) {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "generic.botnoperm"), false);
                        message.getChannel().sendMessage(err.build()).queue();
                    }
                }
                return;
            }
        }
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.plsjoin"), false);
        message.getChannel().sendMessage(err.build()).queue();
    }
}