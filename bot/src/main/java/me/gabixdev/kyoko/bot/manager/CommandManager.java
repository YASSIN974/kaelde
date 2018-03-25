package me.gabixdev.kyoko.bot.manager;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.command.CommandType;
import me.gabixdev.kyoko.bot.util.CommonErrors;
import me.gabixdev.kyoko.bot.util.StringUtil;
import me.gabixdev.kyoko.shared.Settings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandManager {
    private final Kyoko kyoko;

    private Set<Command> registered;
    private Map<String, Command> commands;

    public CommandManager(Kyoko kyoko) {
        this.registered = new HashSet<Command>();
        this.commands = new HashMap<>();
        this.kyoko = kyoko;
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

        if (commands.keySet().contains(command.getName()) || (!aliases.isEmpty() && commands.keySet().containsAll(aliases)))
            throw new IllegalArgumentException("Alias or label is already registered!");

        registered.add(command);
        commands.put(command.getName(), command);

        aliases.forEach(alias -> {
            commands.put(alias, command);
        });
    }

    public void handlePrivate(MessageReceivedEvent event) {
        handlePrefix(event, true);
    }

    public void handleGuild(MessageReceivedEvent event) {
        handlePrefix(event, false);
    }

    private void handlePrefix(MessageReceivedEvent event, boolean direct) {
        String content = event.getMessage().getContentRaw();

        Settings s = kyoko.getSettings();

        if (content.startsWith(s.normalPrefix)) {
            content = content.trim().substring(s.normalPrefix.length()).trim();
            handleNormal(event, s.normalPrefix, content, direct);
        } else if (content.startsWith(s.debugPrefix)) {
            if (s.owner.equals(event.getAuthor().getId())) {
                content = content.trim().substring(s.debugPrefix.length()).trim();
                handleDebug(event, s.debugPrefix, content, direct);
            }
        }
    }

    private void handleNormal(MessageReceivedEvent event, String prefix, String content, boolean direct) {
        String[] parts = content.split(" ");
        if (parts.length != 0) {
            Command c = commands.get(parts[0].toLowerCase());
            if (c != null && c.getType() == CommandType.NORMAL) {
                if (!c.isAllowInDMs() && direct) return;

                String[] args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);

                String concatArgs = String.join(" ", args);
                CommandContext context = new CommandContext(kyoko, c, event, prefix, parts[0], concatArgs, args);

                kyoko.getExecutor().submit(() -> {
                    kyoko.getLogger().info("User " + StringUtil.formatDiscrim(event.getAuthor()) + "(" + event.getAuthor().getId() + ") on guild " + event.getGuild().getName() + "(" + event.getGuild().getId() + ") executed " + content);
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
        String[] parts = content.split(" ");
        if (parts.length != 0) {
            Command c = commands.get(parts[0].toLowerCase());
            if (c != null && c.getType() == CommandType.DEBUG) {
                if (!c.isAllowInDMs() && direct) return;

                String[] args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);

                String concatArgs = String.join(" ", args);
                CommandContext context = new CommandContext(kyoko, c, event, prefix, parts[0], concatArgs, args);

                kyoko.getExecutor().submit(() -> {
                    kyoko.getLogger().info("User " + StringUtil.formatDiscrim(event.getAuthor()) + "(" + event.getAuthor().getId() + ") on guild " + event.getGuild().getName() + "(" + event.getGuild().getId() + ") executed debug command " + content);
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
