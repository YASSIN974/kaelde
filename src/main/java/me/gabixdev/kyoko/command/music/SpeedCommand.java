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

public class SpeedCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"speed"};

    public SpeedCommand(Kyoko kyoko) {
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
        return "music.speed.description";
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

        if (args.length != 1) {
            EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

            double speedd = Double.parseDouble(args[1]);
            int speed = 10;
            if (speedd < 1.0 || speedd > 2.0) {
                eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.speedoutofrange"), false);
            } else {
                speed = (int) Math.floor(speedd * 10);
                musicManager.getSendHandler().setSpeed(speed);
                eb.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.speedset"), speed), false);
            }

            message.getTextChannel().sendMessage(eb.build()).queue();
        } else {
            printUsage(kyoko, l, message.getTextChannel());
        }
    }
}
