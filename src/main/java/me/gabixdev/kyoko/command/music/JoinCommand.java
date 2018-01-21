package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;

public class JoinCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"join"};

    public JoinCommand(Kyoko kyoko) {
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
        return "music.join.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        for (VoiceChannel voiceChannel : message.getGuild().getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(message.getMember())) {
                message.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                //System.out.println("connected: " +message.getGuild().getAudioManager().isConnected());
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                Language l = kyoko.getI18n().getLanguage(message.getGuild());
                err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.joined"), voiceChannel.getName()), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            }
        }
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getGuild());
        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.plsjoin"), false);
        message.getChannel().sendMessage(err.build()).queue();
    }
}
