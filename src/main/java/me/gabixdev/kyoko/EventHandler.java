package me.gabixdev.kyoko;

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

    public EventHandler(Kyoko kyoko) {
        this.kyoko = kyoko;
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
            } else {
                kyoko.getCommandManager().parseAndExecute(e);
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
