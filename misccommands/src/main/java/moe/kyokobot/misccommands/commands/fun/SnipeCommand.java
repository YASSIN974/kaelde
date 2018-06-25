package moe.kyokobot.misccommands.commands.fun;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class SnipeCommand extends Command {
    private Long2ObjectOpenHashMap<Snipe> snipes;
    private final Pattern inviteRegex = Pattern.compile("(https?://)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com/invite)/.+[a-z]", CASE_INSENSITIVE);
    private final EventBus eventBus;

    public SnipeCommand(EventBus eventBus) {
        this.eventBus = eventBus;

        name = "snipe";
        description = "snipe.description";
        category = CommandCategory.FUN;
    }

    @Override
    public void onRegister() {
        snipes = new Long2ObjectOpenHashMap<>();
        eventBus.register(this);
    }

    @Override
    public void onUnregister() {
        snipes = null;
        try {
            eventBus.unregister(this);
        } catch (IllegalArgumentException ignored) {
            // because there's no way to check the handler was already (un)registered, we will just ignore the exception.
        }
    }

    @Override
    public void execute(CommandContext context) {
        Snipe snipe = snipes.get(context.getGuild().getIdLong());
        if (snipe != null) {
            Message message = snipe.snipedMessages.remove(context.getChannel().getIdLong());
            if (message != null) {
                EmbedBuilder eb = context.getNormalEmbed();
                eb.setTitle(String.format(context.getTranslated("snipe.said"), UserUtil.toDiscrim(message.getAuthor())));
                eb.setDescription(message.getAttachments().stream().map(Message.Attachment::getUrl).collect(Collectors.joining("\n")) + inviteRegex.matcher(message.getContentRaw()).replaceAll("[CENSORED]"));
                eb.setFooter(String.format(context.getTranslated("snipe.snipedby"), UserUtil.toDiscrim(context.getSender())), context.getSender().getEffectiveAvatarUrl());
                context.send(eb.build());
                return;
            }
        }
        context.send(CommandIcons.error + context.getTranslated("snipe.nosnipes"));
    }

    @Subscribe
    public void onMessage(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;

        Snipe snipe = snipes.get(event.getGuild().getIdLong());
        if (snipe != null) {
            if (snipe.lastMessages.size() == 50)
                snipe.lastMessages.remove(0);
        } else {
            snipe = new Snipe();
            snipes.put(event.getGuild().getIdLong(), snipe);
        }

        snipe.lastMessages.put(event.getMessageIdLong(), event.getMessage());
    }

    @Subscribe
    public void onDelete(MessageDeleteEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;

        Snipe snipe = snipes.get(event.getGuild().getIdLong());
        if (snipe != null) {
            snipe.snipedMessages.remove(event.getChannel().getIdLong());
        } else {
            snipe = new Snipe();
            snipes.put(event.getGuild().getIdLong(), snipe);
        }

        Message m = snipe.lastMessages.remove(event.getMessageIdLong());
        if (m != null)
            snipe.snipedMessages.put(event.getChannel().getIdLong(), m);
    }

    @Subscribe
    public void onEdit(MessageUpdateEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;

        Snipe snipe = snipes.get(event.getGuild().getIdLong());
        if (snipe != null) {
            snipe.snipedMessages.remove(event.getChannel().getIdLong());
        } else {
            snipe = new Snipe();
            snipes.put(event.getGuild().getIdLong(), snipe);
        }

        Message m = snipe.lastMessages.remove(event.getMessageIdLong());
        if (m != null)
            snipe.snipedMessages.put(event.getChannel().getIdLong(), m);
    }

    @Subscribe
    public void onLeave(GuildLeaveEvent event) {
        if (snipes != null) snipes.remove(event.getGuild().getIdLong());
    }

    private class Snipe {
        // hack needed cuz JDA doesn't support getMessage() in MessageDeleteEvent
        public Long2ObjectOpenHashMap<Message> lastMessages = new Long2ObjectOpenHashMap<>();
        public Long2ObjectOpenHashMap<Message> snipedMessages = new Long2ObjectOpenHashMap<>();
    }
}