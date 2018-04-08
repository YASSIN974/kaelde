package me.gabixdev.kyoko;

import me.gabixdev.kyoko.database.UserConfig;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.DebugCommands;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

import static me.gabixdev.kyoko.util.StringUtil.*;
import static org.apache.commons.lang3.RandomUtils.*;

public class EventHandler implements EventListener {
    private final Kyoko kyoko;

    public EventHandler(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    // Disclaimer: I don't read logs, they're only read on local debug
    @Override
    public void onEvent(Event event) {
        if (kyoko.isInitialized())
            if (event instanceof MessageReceivedEvent) {
                MessageReceivedEvent e = (MessageReceivedEvent) event;
                if (e.getAuthor().isBot() || e.getMessage().getContentRaw().trim().isEmpty()) return;
                if (e.getChannelType() == ChannelType.PRIVATE) {
                    if (e.getAuthor().getId().equals(kyoko.getSettings().getOwner())) {
                        DebugCommands.handle(kyoko, e);
                    }
                } else {
                    kyoko.getExecutor().submit(() -> {
                        if (e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_WRITE)) kyoko.getCommandManager().parseAndExecute(e);
                        handleMessageReward(e);
                    });
                }
            } else if (event instanceof MessageDeleteEvent) {
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
                if (kyoko.getDblApi() != null) kyoko.getDblApi().setStats(kyoko.getJda().getSelfUser().getId(), kyoko.getJda().getGuilds().size());
            } else if (event instanceof GuildLeaveEvent) {
                kyoko.getLog().info("Left guild " + ((GuildLeaveEvent) event).getGuild());
                if (kyoko.getDblApi() != null) kyoko.getDblApi().setStats(kyoko.getJda().getSelfUser().getId(), kyoko.getJda().getGuilds().size());
            }
    }

    private void handleMessageReward(MessageReceivedEvent e) {
        if(e.getMessage().getIdLong() % 20 == 0) {
            try {
                UserConfig config = kyoko.getDatabaseManager().getUser(e.getAuthor());
                if (Math.random() > 0.99) {
                    // JACKPOT!
                    kyoko.getLog().info("User " + logUser(e.getAuthor()) + " hit jackpot! :D");
                    config.money += 1000;
                } else config.money += nextInt(1, 5);
                kyoko.getDatabaseManager().saveUser(e.getAuthor(), config);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
