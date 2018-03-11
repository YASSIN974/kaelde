package me.gabixdev.kyoko.bot.command.basic;

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
    private TreeMap<String, String> cached;

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
        eb.addField(
                String.format(context.getTranslated("help.header.title"), kyoko.getJda().getSelfUser().getName()),
                String.format(context.getTranslated("help.header.desc"), Constants.WIKI_URL, Constants.DISCORD_URL),
                false);

        cached.keySet().forEach(s -> eb.addField(context.getTranslated(s), cached.get(s), false));

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

        cached = new TreeMap<>();

        for (CommandCategory t : categories.keySet()) {
            HashSet<Command> set = categories.get(t);
            if (set.size() == 0) continue;

            List<String> s = new ArrayList<>();
            for (Command c : set) {
                if (c != null)
                    s.add("`" + c.getName() + "`");
            }
            String listed = String.join(", ", s);
            cached.put("help.category." + t.name().toLowerCase(), listed);
        }
    }
}
