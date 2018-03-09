package me.gabixdev.kyoko.bot.event;

import me.gabixdev.kyoko.bot.Kyoko;
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
            MessageReceivedEvent mre = ((MessageReceivedEvent) event);
            if (mre.getMessage().getContentRaw().equalsIgnoreCase("test")) {
                mre.getTextChannel().sendMessage("TEST RELOADU KODU UWU").queue();
            }
        }
    }
}
