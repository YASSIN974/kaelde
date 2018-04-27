package moe.kyokobot.bot.manager.impl;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class CommandManagerImpl implements CommandManager {
    private Settings settings;
    private Logger logger;
    private I18n i18n;
    private ScheduledExecutorService executor;

    private Set<Command> registered;
    private Map<String, Command> commands;

    public CommandManagerImpl(Settings settings, I18n i18n, ScheduledExecutorService executor) {
        logger = LoggerFactory.getLogger(getClass());
        this.registered = new HashSet<>();
        this.commands = new HashMap<>();
        this.settings = settings;
        this.i18n = i18n;
        this.executor = executor;
    }

    public Set<Command> getRegistered() {
        return registered;
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void registerCommand(Command command) {
        if (command == null) return;

        List<String> aliases = Arrays.asList(command.getAliases());

        if (commands.keySet().contains(command.getName().toLowerCase()) || (!aliases.isEmpty() && commands.keySet().containsAll(aliases))) {
        /*    Command c = commands.get(command.getName());
            commands.values().removeIf(cmd -> cmd == c);*/
            throw new IllegalArgumentException("Alias or label is already registered!");
        }
        registered.add(command);
        commands.put(command.getName().toLowerCase(), command);

        aliases.forEach(alias -> {
            commands.put(alias, command);
        });
    }

    public void unregisterCommand(Command command) {
        if (command == null) return;

        registered.remove(command);
        commands.values().remove(command);
    }

    public void unregisterAll() {
        registered = new HashSet<>();
        commands = new HashMap<>();
    }

    @Subscribe
    private void onMessage(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getChannelType() == ChannelType.TEXT) {
            handlePrefix(event, false);
        } else if (event.getChannelType() == ChannelType.PRIVATE) {
            handlePrefix(event, true);
        }
    }

    private void handlePrefix(MessageReceivedEvent event, boolean direct) {
        String content = event.getMessage().getContentRaw();

        if (content.startsWith(settings.bot.normalPrefix)) {
            content = content.trim().substring(settings.bot.normalPrefix.length()).trim();
            handleNormal(event, settings.bot.normalPrefix, content, direct);
        } else if (content.startsWith(settings.bot.debugPrefix)) {
            if (settings.bot.owner.equals(event.getAuthor().getId())) {
                content = content.trim().substring(settings.bot.debugPrefix.length()).trim();
                handleDebug(event, settings.bot.debugPrefix, content, direct);
            }
        }
    }

    private void handleNormal(MessageReceivedEvent event, String prefix, String content, boolean direct) {
        List<String> parts = Splitter.on(CharMatcher.breakingWhitespace()).splitToList(content);

        if (parts.size() != 0) {
            Command c = commands.get(parts.get(0).toLowerCase());
            if (c != null && c.getType() == CommandType.NORMAL) {
                if (!c.isAllowInDMs() && direct) return;

                String[] args = parts.stream().skip(1).toArray(String[]::new);
                String concatArgs = Joiner.on(" ").join(args);

                CommandContext context = new CommandContext(settings, i18n, c, event, prefix, parts.get(0).toLowerCase(), concatArgs, args);

                executor.submit(() -> {
                    logger.info("User " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getId() + ") on guild " + event.getGuild().getName() + "(" + event.getGuild().getId() + ") executed " + content);
                    try {
                        c.execute(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonErrors.exception(context);
                    }
                });
            }
        }
    }

    private void handleDebug(MessageReceivedEvent event, String prefix, String content, boolean direct) {
        List<String> parts = Splitter.on(CharMatcher.breakingWhitespace()).splitToList(content);

        if (parts.size() != 0) {
            Command c = commands.get(parts.get(0).toLowerCase());
            if (c != null && c.getType() == CommandType.DEBUG) {
                if (!c.isAllowInDMs() && direct) return;

                String[] args = parts.stream().skip(1).toArray(String[]::new);
                String concatArgs = Joiner.on(" ").join(args);

                CommandContext context = new CommandContext(settings, i18n, c, event, prefix, parts.get(0).toLowerCase(), concatArgs, args);

                executor.submit(() -> {
                    logger.info("User " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " (" + event.getAuthor().getId() + ") on guild " + event.getGuild().getName() + "(" + event.getGuild().getId() + ") executed " + content);
                    try {
                        c.execute(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonErrors.exception(context);
                    }
                });
            }
        }
    }
}
