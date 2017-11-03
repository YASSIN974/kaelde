package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.*;

public class HelpCommand extends Command {
    private final Kyoko kyoko;
    private final String[] aliases = new String[] {"help"};
    private HashMap<CommandType, HashSet<Command>> categories;
    private TreeMap<String, String> cached;

    public HelpCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        categories = null;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "help.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.BASIC;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        if (categories == null) {
            initCategories();
        }

        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getGuild());
        normal.addField(kyoko.getI18n().get(l, "help.header.title"), String.format(kyoko.getI18n().get(l, "help.header.desc"), Constants.WIKI_URL), false);

        for (String s : cached.keySet()) {
            normal.addField(kyoko.getI18n().get(l, s), cached.get(s), false);
        }

        //normal.addField("_Command system is being rewritten, not ready to use_", "", false);
        //normal.addField(kyoko.getI18n().get(l, "help.header.commands"), ls.substring(0, ls.length() - 1), true);
        //normal.addField(kyoko.getI18n().get(l, "help.header.description"), ds.substring(0, ds.length() - 1), true);

        message.getChannel().sendMessage(normal.build()).queue();
    }

    private void initCategories() {
        categories = new HashMap<>();

        for (CommandType t : CommandType.values()) {
            HashSet<Command> cmds = new HashSet<>();
            for (Command c : kyoko.getCommandManager().getCommands()) {
                if (c.getType() == t) {
                    cmds.add(c);
                }
            }
            categories.put(t, cmds);
        }

        cached = new TreeMap<>();

        for (CommandType t : categories.keySet()) {
            HashSet<Command> set = categories.get(t);
            if (set.size() == 0) continue;

            List<String> s = new ArrayList<>();
            for (Command c : set) {
                if (c != null)
                    s.add("`" + c.getLabel() + "`");
            }
            String listed = String.join(", ", s);
            cached.put("help.category." + t.name().toLowerCase(), listed);
        }
    }
}
