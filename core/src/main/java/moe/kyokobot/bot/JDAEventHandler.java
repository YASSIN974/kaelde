package moe.kyokobot.bot;

import moe.kyokobot.bot.manager.CommandManager;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class JDAEventHandler implements EventListener {
    private CommandManager commandManager;

    public JDAEventHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            onMessage((MessageReceivedEvent) event);
        } else if (event instanceof GuildJoinEvent) {

        } else if (event instanceof GuildLeaveEvent) {

        }
    }

    private void onMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getChannelType() == ChannelType.TEXT) {
            commandManager.handleGuild(event);
        } else if (event.getChannelType() == ChannelType.PRIVATE) {
            commandManager.handlePrivate(event);
        }
    }
}
