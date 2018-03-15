package me.gabixdev.kyoko;

import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.util.command.DebugCommands;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.RandomUtils;

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
        if (kyoko.isInitialized())
            if (event instanceof MessageReceivedEvent) {
                MessageReceivedEvent e = (MessageReceivedEvent) event;
                if (e.getAuthor().isBot()) return;
                if (e.getChannelType() == ChannelType.PRIVATE) {
                    if (e.getAuthor().getId().equals(kyoko.getSettings().getOwner())) {
                        DebugCommands.handle(kyoko, e);
                    }
                } else {
                    if(e.getMessage().getIdLong() % 20 == 0) {
                        try {
                            UserConfig config = kyoko.getDatabaseManager().getUser(e.getAuthor());
                            config.money += RandomUtils.nextInt(1, 5);
                            kyoko.getDatabaseManager().saveUser(e.getAuthor(), config);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    kyoko.getExecutor().submit(() -> {
                        kyoko.getCommandManager().parseAndExecute(e);
                    });
                }
            } else if (event instanceof GuildVoiceLeaveEvent) {
                List<Member> members = ((GuildVoiceLeaveEvent) event).getChannelLeft().getMembers();
                if (members.size() == 1) {
                    if (members.get(0).getUser().getIdLong() == kyoko.getJda().getSelfUser().getIdLong()) {
                        ((GuildVoiceLeaveEvent) event).getGuild().getAudioManager().setSendingHandler(null);
                        ((GuildVoiceLeaveEvent) event).getGuild().getAudioManager().closeAudioConnection();
                    }
                }
            } else if (event instanceof GuildJoinEvent) {
                kyoko.getLog().info("Joined guild " + ((GuildJoinEvent) event).getGuild());
            } else if (event instanceof GuildLeaveEvent) {
                kyoko.getLog().info("Left guild " + ((GuildLeaveEvent) event).getGuild());
            }
    }
}
