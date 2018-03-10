package me.gabixdev.kyoko.bot.manager;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.shared.Settings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandManager {
    private final Kyoko kyoko;

    private HashMap<String, Command> commands;

    public CommandManager(Kyoko kyoko) {
        this.commands = new HashMap<>();
        this.kyoko = kyoko;
    }

    public void registerCommand(Command command) {
        List<String> aliases = Arrays.asList(command.getAliases());

        if (commands.keySet().contains(command.getName()) || commands.keySet().containsAll(Arrays.asList(command.getAliases())))
            throw new IllegalArgumentException("Alias or label is already registered!");

        commands.put(command.getName(), command);

    }

    public void handlePrivate(MessageReceivedEvent event) {

    }

    public void handleGuild(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();

        Settings s = kyoko.getSettings();

        if (content.startsWith(s.normalPrefix)) {
            content = content.trim().substring(s.normalPrefix.length()).trim();
            handleNormal(event, content);
        }
    }

    private void handleNormal(MessageReceivedEvent event, String content) {
        String[] parts = content.split(" ");
        if (parts.length != 0) {
            Command c = commands.get(parts[0].toLowerCase());
            if (c != null) {
                String[] args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);

                String concatArgs = String.join(" ", args);
                CommandContext context = new CommandContext(event, parts[0], concatArgs, args);

                kyoko.getExecutor().submit(() -> {
                    c.execute(context);
                });
            }
        }
    }
}
