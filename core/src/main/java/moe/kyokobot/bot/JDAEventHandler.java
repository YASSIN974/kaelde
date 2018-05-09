package moe.kyokobot.bot;

import com.google.common.eventbus.EventBus;
import moe.kyokobot.bot.manager.CommandManager;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class JDAEventHandler implements EventListener {
    private EventBus eventBus;

    public JDAEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            if (!((MessageReceivedEvent) event).getAuthor().isBot())
                eventBus.post(event);
        } else if (event instanceof GuildJoinEvent) {
            eventBus.post(event);
        } else if (event instanceof GuildLeaveEvent) {
            eventBus.post(event);
        }
    }
}
