package moe.kyokobot.misccommands.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.EmbedBuilder;

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
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            EmbedBuilder eb = context.getNormalEmbed();
            eb.addField(context.getSettings().bot.botIcon + " " +
                    (context.getSettings().bot.botName.equals("Kyoko")
                            ? context.getTranslated("help.header.title")
                            : String.format(context.getTranslated("help.header.title.cust"), context.getSettings().bot.botName)),
                    String.format(context.getTranslated("help.header.subtitle"), Constants.COMMANDS_URL), false);

            TreeMap<CommandCategory, List<String>> categories = Arrays.stream(CommandCategory.values()).collect(Collectors.toMap(c -> c, c -> new ArrayList<>(), (a, b) -> b, TreeMap::new));
            commandManager.getRegistered().stream().filter(command -> command.getCategory() != null).forEach(command -> categories.get(command.getCategory()).add(command.getName()));

            categories.forEach((category, commands) -> {
                if (commands.size() != 0) {
                    commands.sort(Ordering.usingToString());
                    eb.addField(context.getTranslated("help.category." + category.name().toLowerCase()) + " - (" + commands.size() + ")", "`" + Joiner.on("`, `").join(commands) + "`", false);
                }
            });

            context.send(eb.build());
        }
    }
}
