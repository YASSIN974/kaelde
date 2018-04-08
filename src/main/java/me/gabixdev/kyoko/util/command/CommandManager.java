package me.gabixdev.kyoko.util.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.database.GuildConfig;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.ObjectOutputStream;
import java.util.*;

public class CommandManager {
    private final Kyoko kyoko;

    private final Gson gson = new GsonBuilder().create();

    private String mention;
    private long runs;

    private HashSet<Command> commands;
    private HashMap<String, Command> handlers;
    private HashMap<Command, String> disabled;
    private HashMap<Guild, List<String>> prefixes;

    public CommandManager(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.commands = new HashSet<>();
        this.handlers = new HashMap<>();
        this.prefixes = new HashMap<>();
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

    public Command getCommand(String label) {
        return handlers.get(label);
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
            } else if (content.toLowerCase().startsWith(kyoko.getSettings().getPrefix())) {
                bits = content.substring(kyoko.getSettings().getPrefix().length()).trim().split(" ");
            } else {
                List<String> table = getPrefixes(event.getGuild());
                exit:
                if (table.size() != 0) {
                    for (String prefix : table) {
                        if (content.toLowerCase().startsWith(prefix)) {
                            bits = content.substring(prefix.length()).trim().split(" ");
                            break exit;
                        }
                    }
                    return;
                } else return;
            }

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

    public List<String> getPrefixes(Guild g) {
        if (!prefixes.containsKey(g)) {
            List<String> pfxs = Collections.emptyList();
            try {
                GuildConfig gc = kyoko.getDatabaseManager().getGuild(g);
                List<String> list = (ArrayList<String>) gson.fromJson(gc.prefixes, new TypeToken<ArrayList<String>>() {}.getType());
                pfxs = list;
            } catch (Exception e) {
                e.printStackTrace();
            }

            prefixes.put(g, pfxs);
        }

        return prefixes.get(g);
    }

    public void setPrefixes(Guild g, List<String> prefixlist) {
        if (prefixes.containsKey(g)) {
            prefixes.replace(g, prefixlist);
        } else prefixes.put(g, prefixlist);

        try {
            GuildConfig gc = kyoko.getDatabaseManager().getGuild(g);
            gc.prefixes = gson.toJson(prefixlist);
            kyoko.getDatabaseManager().saveGuild(g, gc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
