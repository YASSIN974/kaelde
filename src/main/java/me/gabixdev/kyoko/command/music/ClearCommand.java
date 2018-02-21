package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.music.MusicUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;

public class ClearCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"clear"};

    public ClearCommand(Kyoko kyoko) {
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
        return "music.clear.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
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

        if (musicManager.scheduler.getQueue().isEmpty()) {
            musicManager.player.stopTrack();
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.empty"), kyoko.getSettings().getPrefix(), kyoko.supportedSources, kyoko.getSettings().getPrefix(), kyoko.getSettings().getPrefix()), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }

        musicManager.player.stopTrack();
        musicManager.scheduler.getQueue().clear();
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        err.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.cleared"), false);
        message.getChannel().sendMessage(err.build()).queue();
        System.gc(); // free memory
    }
}
