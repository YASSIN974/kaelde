package me.gabixdev.kyoko.bot.command.normal.basic;

import me.gabixdev.kyoko.bot.Constants;
import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.*;

public class HelpCommand extends Command {
    private final Kyoko kyoko;
    private HashMap<CommandCategory, HashSet<Command>> categories;
    private HashMap<String, Integer> cachedCounts;
    private TreeMap<String, String> cachedLists;

    public HelpCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "help";
        this.aliases = new String[]{"?"};
        this.description = "help.description";
        this.usage = "help.usage";
        this.category = CommandCategory.BASIC;
    }

    @Override
    public void execute(CommandContext context) {
        if (categories == null) {
            initCategories();
        }

        EmbedBuilder eb = context.getNormalEmbed();
        if (context.getArgs().length == 0) {
            eb.addField(
                    String.format(context.getTranslated("help.header.title"), kyoko.getSettings().brand.botName),
                    String.format(context.getTranslated("help.header.desc"), Constants.WIKI_URL, kyoko.getSettings().normalPrefix, kyoko.getSettings().moderationPrefix, kyoko.getSettings().moderationPrefix, Constants.DISCORD_URL),
                    false);

            cachedLists.keySet().forEach(s -> eb.addField(context.getTranslated(s) + " (" + cachedCounts.get(s) + ")", cachedLists.get(s), false));
        } else {
            if (kyoko.getCommandManager().getCommands().keySet().contains(context.getConcatArgs().toLowerCase())) {
                Command c = kyoko.getCommandManager().getCommands().get(context.getConcatArgs().toLowerCase());

                StringBuilder dsc = new StringBuilder();

                dsc.append(context.getTranslated(c.getDescription())).append("\n\n");
                if (c.getAliases().length != 0) {
                    dsc.append(context.getTranslated("help.aliases"))
                            .append(": `")
                            .append(String.join(", ", c.getAliases()))
                            .append("`\n\n");
                }
                dsc.append(context.getTranslated("generic.usage"))
                        .append(": `")
                        .append(kyoko.getSettings().normalPrefix)
                        .append(c.getName());
                if (c.getUsage() != null) dsc.append(" ").append(context.getTranslated(c.getUsage()));
                dsc.append("`");

                eb.addField(context.getTranslated("help.header.titlealt") + kyoko.getSettings().normalPrefix + c.getName(),
                        dsc.toString(),
                        false);
            }
        }
        context.send(eb.build());
    }

    private void initCategories() {
        categories = new HashMap<>();

        for (CommandCategory t : CommandCategory.values()) {
            HashSet<Command> cmds = new HashSet<>();
            kyoko.getCommandManager().getRegistered().stream()
                    .filter(command -> (command.getType() == CommandType.NORMAL && command.getCategory() == t))
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
                    s.add("`" + c.getName() + "`");
            }
            String listed = String.join(", ", s);
            cachedLists.put("help.category." + t.name().toLowerCase(), listed);
            cachedCounts.put("help.category." + t.name().toLowerCase(), set.size());
        }
    }
}
