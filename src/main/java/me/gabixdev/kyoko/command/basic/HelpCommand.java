package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.*;

public class HelpCommand extends Command {
    private final Kyoko kyoko;
    private final String[] aliases = new String[] {"help"};
    private HashMap<CommandCategory, HashSet<Command>> categories;
    private HashMap<String, Integer> cachedCounts;
    private TreeMap<String, String> cachedLists;

    public HelpCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
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
    public CommandCategory getCategory() {
        return CommandCategory.BASIC;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        if (categories == null) {
            initCategories();
        }

        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        if (categories == null) {
            initCategories();
        }

        if (args.length == 1) {
            eb.addField(
                    String.format(kyoko.getI18n().get(l, "help.header.title"), kyoko.getJda().getSelfUser().getName()),
                    String.format(kyoko.getI18n().get(l, "help.header.desc"), Constants.WIKI_URL, kyoko.getSettings().getPrefix(), Constants.DISCORD_URL),
                    false);

            for (String s : cachedLists.keySet()) {
                eb.addField(kyoko.getI18n().get(l, s) + " (" + cachedCounts.get(s) + ")", cachedLists.get(s), false);
            }
            eb.addField("", String.format(kyoko.getI18n().get(l, "help.footer"), kyoko.getSettings().getPrefix()), false);

        } else {
            String clabel = String.join(" ", args).substring(args[0].length() + 1);
            Command c = kyoko.getCommandManager().getCommands().stream().filter(command -> command.getLabel().equalsIgnoreCase(clabel) || Arrays.asList(command.getAliases()).contains(clabel.toLowerCase())).findFirst().orElse(null);
            if (c != null) {
                StringBuilder dsc = new StringBuilder();

                dsc.append(kyoko.getI18n().get(l, c.getDescription())).append("\n\n");
                if (c.getAliases().length != 0) {
                    dsc.append(kyoko.getI18n().get(l, "help.aliases"))
                            .append(": `")
                            .append(String.join(", ", c.getAliases()))
                            .append("`\n\n");
                }
                dsc.append(kyoko.getI18n().get(l, "generic.usage"))
                        .append(": `")
                        .append(kyoko.getSettings().getPrefix())
                        .append(c.getLabel());
                if (c.getUsage() != null) dsc.append(" ").append(kyoko.getI18n().get(l, c.getUsage()));
                dsc.append("`");

                eb.addField(kyoko.getI18n().get(l, "help.header.titlealt") + kyoko.getSettings().getPrefix() + c.getLabel(),
                        dsc.toString(),
                        false);
            } else {
                eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "help.notfound"), false);
            }
        }
        message.getTextChannel().sendMessage(eb.build()).queue();
    }

    private void initCategories() {
        categories = new HashMap<>();

        for (CommandCategory t : CommandCategory.values()) {
            HashSet<Command> cmds = new HashSet<>();
            kyoko.getCommandManager().getCommands().stream()
                    .filter(command -> (command.getCategory() == t))
                    .forEach(cmds::add);
            categories.put(t, cmds);
        }

        cachedLists = new TreeMap<>();
        cachedCounts = new HashMap<>();

        for (CommandCategory t : categories.keySet()) {
            HashSet<Command> set = categories.get(t);
            if (set.size() == 0) continue;

            List<String> s = new ArrayList<>();
            for (Command c : set) {
                if (c != null)
                    s.add("`" + c.getLabel() + "`");
            }
            String listed = String.join(", ", s);
            cachedLists.put("help.category." + t.name().toLowerCase(), listed);
            cachedCounts.put("help.category." + t.name().toLowerCase(), set.size());
        }
    }
}
