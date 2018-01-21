package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

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
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        for (VoiceChannel voiceChannel : message.getGuild().getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(message.getMember())) {
                try {
                    message.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                    //System.out.println("connected: " +message.getGuild().getAudioManager().isConnected());
                    EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                    err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.joined"), voiceChannel.getName()), false);
                    message.getChannel().sendMessage(err.build()).queue();
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
