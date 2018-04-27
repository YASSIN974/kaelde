package moe.kyokobot.bot.discordapi.impl;

import com.google.common.eventbus.EventBus;
import moe.kyokobot.bot.discordapi.DiscordAPI;
import moe.kyokobot.bot.discordapi.entity.Guild;
import moe.kyokobot.bot.discordapi.entity.TextChannel;
import moe.kyokobot.bot.discordapi.event.GuildCountUpdateEvent;
import moe.kyokobot.bot.util.JDAUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class JDAEventHandler implements EventListener {
    private final EventBus eventBus;
    private final DiscordAPI discordAPI;

    public JDAEventHandler(EventBus eventBus, DiscordAPI discordAPI) {
        this.eventBus = eventBus;
        this.discordAPI = discordAPI;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            eventBus.post(event);
        } else if (event instanceof GuildJoinEvent) {
            GuildJoinEvent ev = (GuildJoinEvent) event;
            eventBus.post(new GuildCountUpdateEvent(event.getJDA().getGuilds().size()));
        } else if (event instanceof GuildLeaveEvent) {
            eventBus.post(new GuildCountUpdateEvent(event.getJDA().getGuilds().size()));
        }
    }
}
