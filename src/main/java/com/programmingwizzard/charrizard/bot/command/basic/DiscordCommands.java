package com.programmingwizzard.charrizard.bot.command.basic;

import com.programmingwizzard.charrizard.bot.Charrizard;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import pl.themolka.commons.command.Command;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandUsageException;
import pl.themolka.commons.command.Commands;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class DiscordCommands extends Commands implements EventListener {

    private final Charrizard charrizard;

    public DiscordCommands(Charrizard charrizard) {
        this.charrizard = charrizard;
    }

    @Override
    public void handleCommand(Message message, CommandContext context) {
        try {
            if (context.getCommand().getMin() > context.getParamsLength()) {
                throw new CommandUsageException(context.getCommand().getUsage());
            }
            charrizard.run(message.getGuild(), () -> {
                try {
                    context.getCommand().handleCommand(message, context);
                } catch (Throwable throwable) {
                    message.getChannel().sendMessage(getErrorBuilder().addField("Error", throwable.getMessage(), false).build());
                }
            });
        } catch (CommandUsageException ex) {
            message.getChannel().sendMessage(getErrorBuilder().addField("Usage", ex.getMessage(), false).build()).queue();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof MessageReceivedEvent)) {
            return;
        }
        MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
        String[] args = messageEvent.getMessage().getContent().split(" ");
        if (!args[0].startsWith("!")) {
            return;
        }
        args[0] = args[0].substring(1);
        Command command = this.getCommand(args[0]);
        if (command == null) {
            return;
        }
        handleCommand(messageEvent.getMessage(), command, args[0], args);
    }
}
