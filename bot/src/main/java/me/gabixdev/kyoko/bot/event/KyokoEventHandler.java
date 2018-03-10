package me.gabixdev.kyoko.bot.event;

import me.gabixdev.kyoko.bot.Kyoko;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class KyokoEventHandler implements EventListener {
    private final Kyoko kyoko;

    public KyokoEventHandler(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            onMessage((MessageReceivedEvent) event);
        }
    }

    private void onMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getChannelType() == ChannelType.TEXT) {
            kyoko.getCommandManager().handleGuild(event);
        } else if (event.getChannelType() == ChannelType.PRIVATE) {
            kyoko.getCommandManager().handlePrivate(event);
        }
    }
}
