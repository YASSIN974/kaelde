package moe.kyokobot.misccommands.commands.basic;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    private CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        name = "help";
        category = CommandCategory.BASIC;
        usage = "";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            if (context.getGuild().getSelfMember().hasPermission(context.getChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                sendEmbed(context);
            } else {
                sendFallback(context);
            }
        }
    }

    private void sendFallback(CommandContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommandIcons.ERROR).append(context.getTranslated("help.fallback")).append("\n\n```markdown\n");
        sb.append("< ").append(context.getSettings().bot.botName.equals("Kyoko")
                        ? context.getTranslated("help.header.title")
                        : String.format(context.getTranslated("help.header.title.cust"), context.getSettings().bot.botName)).append(" >");

        TreeMap<CommandCategory, List<String>> categories = Arrays.stream(CommandCategory.values())
                .collect(Collectors.toMap(c -> c, c -> new ArrayList<>(), (a, b) -> b, TreeMap::new));
        commandManager.getRegistered().stream().filter(command -> command.getCategory() != null
                && (!command.isExperimental() || commandManager.isExperimental(context.getGuild())))
                .forEach(command -> categories.get(command.getCategory()).add(command.getName()));

        categories.forEach((category, commands) -> {
            if (!commands.isEmpty()) {
                commands.sort(Ordering.usingToString());
                sb.append("\n\n# ");
                sb.append(context.getTranslated("help.category." + category.name().toLowerCase()) + " - (" + commands.size() + ")").append("\n");
                sb.append(Joiner.on(", ").join(commands));
            }
        });
        sb.append("\n```");
        context.send(sb.toString());
    }

    private void sendEmbed(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();
        eb.setTitle(context.getSettings().bot.botIcon + " " +
                        (context.getSettings().bot.botName.equals("Kyoko")
                                ? context.getTranslated("help.header.title")
                                : String.format(context.getTranslated("help.header.title.cust"), context.getSettings().bot.botName)));
        eb.setDescription(String.format(context.getTranslated("help.header.subtitle"), Constants.COMMANDS_URL));

        TreeMap<CommandCategory, List<String>> categories = Arrays.stream(CommandCategory.values())
                .collect(Collectors.toMap(c -> c, c -> new ArrayList<>(), (a, b) -> b, TreeMap::new));
        commandManager.getRegistered().stream().filter(command -> command.getCategory() != null
                && (!command.isExperimental() || commandManager.isExperimental(context.getGuild())))
                .forEach(command -> categories.get(command.getCategory()).add(command.getName()));

        categories.forEach((category, commands) -> {
            if (!commands.isEmpty()) {
                commands.sort(Ordering.usingToString());
                eb.addField(context.getTranslated("help.category." + category.name().toLowerCase())
                        + " - (" + commands.size() + ")", "`" + Joiner.on("`, `").join(commands) + "`", false);
            }
        });

        context.send(eb.build());
    }

    private void sendDM(CommandContext context) {

    }
}
