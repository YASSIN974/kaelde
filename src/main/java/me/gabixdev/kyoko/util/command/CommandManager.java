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

    private final String pref;
    private String mention;
    private int prefLen;
    private long runs;

    private HashSet<Command> commands;
    private HashMap<String, Command> handlers;

    public CommandManager(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.pref = kyoko.getSettings().getPrefix();
        this.prefLen = this.pref.length();
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

        if (content.isEmpty()) return;

        if (content.startsWith(mention)) {
            if (content.equalsIgnoreCase(mention)) { // print help on mention
                bits = new String[]{"help"};
            } else { // handle command if it's specified after mention
                String[] args = new String[bits.length - 1];
                System.arraycopy(bits, 1, args, 0, args.length);
                bits = args;
            }
        } else if (content.toLowerCase().startsWith(pref)) { // check for prefix
            bits[0] = bits[0].substring(prefLen).trim(); // remove prefix from command label
        } else {
            return;
        }

        Command c = kyoko.getCommandManager().getHandler(bits[0]);
        if (c != null) {
            if (kyoko.getSettings().isLimitExecution()) {
                if (!kyoko.getSettings().getDevs().contains(event.getAuthor().getId())) {
                    CommonErrorUtil.devOnly(kyoko, l, channel);
                }
            } else {
                try {
                    runs++;
                    c.handle(event.getMessage(), event, bits);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    CommonErrorUtil.exception(kyoko, l, event.getTextChannel());
                }
            }
        }
    }

    private Command getHandler(String label) {
        label = label.toLowerCase();
        return handlers.getOrDefault(label, null);
    }

    public long getRuns() {
        return runs;
    }

    public HashSet<Command> getCommands() {
        return commands;
    }
}
