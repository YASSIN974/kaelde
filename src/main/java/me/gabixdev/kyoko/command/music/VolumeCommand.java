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

public class VolumeCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"volume", "vol"};

    public VolumeCommand(Kyoko kyoko) {
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
        return "music.vol.description";
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

        if (args.length == 1) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.volume"), musicManager.player.getVolume()), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }

        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        int vol;
        if (args[1].equalsIgnoreCase("earrape")) {
            vol = 185;
            eb.addField(kyoko.getI18n().get(l, "music.title"), "EARRAPE MODE", false);
        } else {
            try {
                vol = Integer.parseUnsignedInt(args[1]);
            } catch (NumberFormatException ex) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "generic.nan"), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            }

            if (vol < 0 || vol > 150) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                err.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.outofrange"), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            }
            eb.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.volumeset"), vol), false);
        }

        musicManager.player.setVolume(vol);

        message.getChannel().sendMessage(eb.build()).queue();
    }
}
