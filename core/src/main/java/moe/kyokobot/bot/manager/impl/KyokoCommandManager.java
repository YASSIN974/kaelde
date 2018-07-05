package moe.kyokobot.bot.manager.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import io.sentry.Sentry;
import lombok.Getter;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.i18n.I18n;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KyokoCommandManager implements CommandManager {
    private final Logger logger;
    private final I18n i18n;
    private final ScheduledExecutorService executor;
    private final DatabaseManager databaseManager;
    private final Cache<Guild, Boolean> experimentalCache = Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).maximumSize(100).build();

    private Set<Command> registered;
    private Map<String, Command> commands;
    @Getter private long runs = 0;

    public KyokoCommandManager(DatabaseManager databaseManager, I18n i18n, ScheduledExecutorService executor) {
        logger = LoggerFactory.getLogger(getClass());
        this.registered = new HashSet<>();
        this.commands = new HashMap<>();
        this.i18n = i18n;
        this.executor = executor;
        this.databaseManager = databaseManager;
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

        for (Method method : command.getClass().getMethods()) {
            try {
                if (method.isAnnotationPresent(SubCommand.class) && method.getParameterCount() == 1) {
                    SubCommand subCommand = method.getAnnotation(SubCommand.class);
                    String name = subCommand.name().isEmpty() ? method.getName() : subCommand.name();
                    command.getSubCommands().put(name.toLowerCase(), method);
                    logger.debug("Registered subcommand: {} -> {}", name, method);
                    for (String alias : subCommand.aliases()) {
                        command.getSubCommands().put(alias.toLowerCase(), method);
                        logger.debug("Registered subcommand: {} -> {}", alias, method);
                    }
                }
            } catch (Exception e) {
                logger.error("Error while registering subcommand!", e);
                Sentry.capture(e);
            }
        }

        registered.add(command);
        commands.put(command.getName().toLowerCase(), command);

        aliases.forEach(alias -> commands.put(alias, command));

        command.onRegister();
        logger.debug("Registered command: {} -> {}", command.getName(), command.toString());
    }

    public void unregisterCommand(Command command) {
        if (command == null) return;
        commands.values().removeIf(cmd -> command == cmd);
        registered.removeIf(cmd -> command == cmd);
        commands.values().removeIf(cmd -> cmd.getName().equals(command.getName()));
        registered.removeIf(cmd -> cmd.getName().equals(command.getName()));

        command.onUnregister();
    }

    public void unregisterAll() {
        for (Command cmd : registered) {
            cmd.onUnregister();
        }

        registered = new HashSet<>();
        commands = new HashMap<>();
    }

    @Subscribe
    public void handleMessage(MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;

        Settings settings = Settings.instance;
        String content = event.getMessage().getContentRaw();

        if (content.startsWith(event.getJDA().getSelfUser().getAsMention())) {
            content = content.trim().substring(event.getJDA().getSelfUser().getAsMention().length()).trim();
            handleNormal(event, event.getJDA().getSelfUser().getAsMention(), content);
        } else if (content.toLowerCase().startsWith(settings.bot.normalPrefix.toLowerCase())) {
            content = content.trim().substring(settings.bot.normalPrefix.length()).trim();
            handleNormal(event, settings.bot.normalPrefix, content);
        } else if (content.toLowerCase().startsWith(settings.bot.debugPrefix.toLowerCase()) && settings.bot.owner.equals(event.getAuthor().getId())) {
            content = content.trim().substring(settings.bot.debugPrefix.length()).trim();
            handleDebug(event, settings.bot.debugPrefix, content);
        }
    }

    private void handleNormal(MessageReceivedEvent event, String prefix, String content) {
        List<String> parts = Splitter.on(CharMatcher.breakingWhitespace()).splitToList(content);

        if (!parts.isEmpty()) {
            Command c = commands.get(parts.get(0).toLowerCase());
            if (c != null && c.getType() == CommandType.NORMAL) {
                if (c.isExperimental() && !isExperimental(event.getGuild())) return;

                String[] args = parts.stream().skip(1).toArray(String[]::new);
                String concatArgs = Joiner.on(" ").join(args);

                CommandContext context = new CommandContext(i18n, c, event, prefix, parts.get(0).toLowerCase(), concatArgs, args);

                executor.submit(() -> {
                    logger.info("User {}#{} ({}) on guild {}({}) executed: {}", event.getAuthor().getName(), event.getAuthor().getDiscriminator(), event.getAuthor().getId(), event.getGuild().getName(), event.getGuild().getId(), content);
                    runs++;
                    try {
                        c.preExecute(context);
                    } catch (Exception e) {
                        logger.error("Caught error while executing command!", e);
                        Sentry.capture(e);
                        CommonErrors.exception(context, e);
                    }
                });
            }
        }
    }

    private void handleDebug(MessageReceivedEvent event, String prefix, String content) {
        List<String> parts = Splitter.on(CharMatcher.breakingWhitespace()).splitToList(content);

        if (!parts.isEmpty()) {
            Command c = commands.get(parts.get(0).toLowerCase());
            if (c != null && c.getType() == CommandType.DEBUG) {
                String[] args = parts.stream().skip(1).toArray(String[]::new);
                String concatArgs = Joiner.on(" ").join(args);

                CommandContext context = new CommandContext(i18n, c, event, prefix, parts.get(0).toLowerCase(), concatArgs, args);

                executor.submit(() -> {
                    logger.info("User {}#{} ({}) on guild {} ({}) executed: {}", event.getAuthor().getName(), event.getAuthor().getDiscriminator(), event.getAuthor().getId(), event.getGuild().getName(), event.getGuild().getId(), content);
                    try {
                        c.preExecute(context);
                    } catch (Exception e) {
                        logger.error("Caught error while executing command!", e);
                        Sentry.capture(e);
                        CommonErrors.exception(context, e);
                    }
                });
            }
        }
    }

    private boolean isExperimental(Guild guild) {
        Boolean b = experimentalCache.getIfPresent(guild);
        if (b == null) {
            try {
                GuildConfig config = databaseManager.getGuild(guild);
                experimentalCache.put(guild, config.isExperimental());
                return config.isExperimental();
            } catch (Exception e) {
                return false;
            }
        } else return b;
    }
}
