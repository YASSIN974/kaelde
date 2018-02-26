package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class CommandManager {
    private Kyoko kyoko;

    private String mention;
    private long runs;

    private HashSet<Command> commands;
    private HashMap<String, Command> handlers;
    private HashMap<Command, String> disabled;

    public CommandManager(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.commands = new HashSet<>();
        this.handlers = new HashMap<>();
        this.runs = 0;
    }

    public void registerCommand(Command c) {
        if (c == null) return;

        commands.removeIf(element -> element.equals(c));
        handlers.entrySet().removeIf(entry -> {
            for (String s : c.getAliases())
                if (s.equalsIgnoreCase(entry.getKey()))
                    return true;
            return false;
        });
        commands.add(c);
        Arrays.stream(c.getAliases()).filter(alias -> alias != null && !alias.isEmpty()).forEach(alias -> handlers.put(alias.toLowerCase(), c));
    }

    public void parseAndExecute(MessageReceivedEvent event) {
        if (mention == null) mention = kyoko.getJda().getSelfUser().getAsMention();

        String content = event.getMessage().getContentRaw().trim();
        TextChannel channel = event.getMessage().getTextChannel();
        Language l = kyoko.getI18n().getLanguage(event.getMember());
        String[] bits = content.split(" ");

        if (!content.isEmpty() && bits.length != 0) {
            if (content.startsWith(mention)) {
                if (content.equalsIgnoreCase(mention))// print help on mention
                    bits = new String[]{"help"};
                else { // handle command if it's specified after mention
                    String[] args = new String[bits.length - 1];
                    System.arraycopy(bits, 1, args, 0, args.length);
                    bits = args;
                }
            } else if (content.toLowerCase().startsWith(kyoko.getSettings().getPrefix())) { // check for prefix
                bits[0] = bits[0].substring(kyoko.getSettings().getPrefix().length()).trim(); // remove prefix from command label
            } else return;

            Command c = getHandler(bits[0]);
            if (c != null) {
                if (kyoko.getSettings().isLimitExecution() && !kyoko.getSettings().getDevs().contains(event.getAuthor().getId())) {
                    CommonErrorUtil.devOnly(kyoko, l, channel);
                    return;
                }

                try {
                    runs++;
                    // only for debugging purposes, I respect your privacy.
                    kyoko.getLog().info("User " + event.getAuthor().getName() + " (" + event.getAuthor().getId() + ") on guild " + event.getGuild().getName() + "(" + event.getGuild().getId() + ") executed: " + content);
                    c.handle(event.getMessage(), event, bits);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    CommonErrorUtil.exception(kyoko, l, event.getTextChannel());
                }
            }
        }
    }

    private Command getHandler(String label) {
        return handlers.getOrDefault(label.toLowerCase(), null);
    }

    public long getRuns() {
        return runs;
    }

    public HashSet<Command> getCommands() {
        return commands;
    }
}
