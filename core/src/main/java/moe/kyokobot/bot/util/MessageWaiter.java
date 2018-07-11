package moe.kyokobot.bot.util;

import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.command.CommandContext;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MessageWaiter {
    private EventWaiter eventWaiter;
    private CommandContext context;

    @Getter @Setter private Consumer<MessageReceivedEvent> messageHandler;
    @Getter @Setter private Runnable timeoutHandler;

    public MessageWaiter(EventWaiter eventWaiter, CommandContext context) {
        this.eventWaiter = eventWaiter;
        this.context = context;
    }

    public void create() {
        eventWaiter.waitForEvent(MessageReceivedEvent.class,
                this::checkMessage,
                this::handleMessage, 15, TimeUnit.SECONDS, this::onTimeout);
    }

    private boolean checkMessage(MessageReceivedEvent event) {
        return event.getTextChannel() != null && event.getTextChannel().equals(context.getChannel())
                && event.getAuthor().equals(context.getSender());
    }

    private void handleMessage(MessageReceivedEvent event) {
        if (messageHandler != null) messageHandler.accept(event);
    }

    private void onTimeout() {
        if (timeoutHandler != null) timeoutHandler.run();
    }
}
