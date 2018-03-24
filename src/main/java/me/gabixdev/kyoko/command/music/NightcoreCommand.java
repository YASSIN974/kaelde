package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.music.MusicUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;

public class NightcoreCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"nightcore"};

    public NightcoreCommand(Kyoko kyoko) {
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
        return "music.nightcore.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
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

        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        if (musicManager.player.getVolume() == 99) {
            eb.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.nightcore.disabled"), false);
            musicManager.player.setVolume(100);
        } else {
            eb.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.nightcore.enabled"), false);
            musicManager.player.setVolume(99);
        }
        message.getTextChannel().sendMessage(eb.build()).queue();
    }
}
