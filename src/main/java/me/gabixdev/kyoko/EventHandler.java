package me.gabixdev.kyoko;

import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.DebugCommands;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.List;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class EventHandler implements EventListener {

    private final Kyoko kyoko;
    private final String pref;
    private int prefLen;

    private String mention;

    public EventHandler(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.pref = kyoko.getSettings().getPrefix();
        this.prefLen = this.pref.length();

        mention = null;
    }

    @Override
    public void onEvent(Event event) {
        if (!kyoko.isInitialized()) return;

        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getAuthor().isBot()) return;

            if (e.getChannelType() == ChannelType.PRIVATE) {
                if (e.getAuthor().getId().equals(kyoko.getSettings().getOwner())) {
                    DebugCommands.handle(kyoko, e);
                }
            }

            if (mention == null) mention = kyoko.getJda().getSelfUser().getAsMention();
            String[] bits = e.getMessage().getContentRaw().split(" ");
            if (bits.length != 0) {
                if (e.getMessage().getContentRaw().startsWith(mention)) {
                    if (bits.length == 1)
                        bits = new String[]{"help"};
                    else {
                        String[] args = new String[bits.length - 1];
                        System.arraycopy(bits, 1, args, 0, args.length);
                        bits = args;
                    }
                } else if (bits[0].toLowerCase().startsWith(pref)) {
                    bits[0] = bits[0].substring(prefLen).trim();
                }

                if (kyoko.getSettings().isLimitExecution()) {
                    if (!kyoko.getSettings().getDevs().contains(e.getAuthor().getId())) {
                        Language l = kyoko.getI18n().getLanguage(e.getMember());
                        e.getMessage().getTextChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "generic.execlimit"), false).build()).queue();
                        return;
                    }
                }

                Command c = kyoko.getCommandManager().getHandler(bits[0]);
                if (c != null) {
                    try {
                        kyoko.getCommandManager().incrementRunCount();
                        c.handle(e.getMessage(), e, bits);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        Language l = kyoko.getI18n().getLanguage(e.getMember());

                        // REKLAMA KURWAAA
                    }
                }
            }
        } else if (event instanceof GuildVoiceLeaveEvent) {
            List<Member> members = ((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers();
            if (members.size() == 1) {
                if (members.get(0).getUser().getIdLong() == kyoko.getJda().getSelfUser().getIdLong()) {
                    ((GuildVoiceLeaveEvent) event).getGuild().getAudioManager().setSendingHandler(null);
                    ((GuildVoiceLeaveEvent) event).getGuild().getAudioManager().closeAudioConnection();
                }
            }
        }
    }
}
