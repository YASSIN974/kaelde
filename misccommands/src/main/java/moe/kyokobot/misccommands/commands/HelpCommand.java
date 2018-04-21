package moe.kyokobot.misccommands.commands;

import com.google.common.base.Joiner;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpCommand extends Command {
    private CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        name = "help";
        category = CommandCategory.BASIC;
        description = "help.description";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            EmbedBuilder eb = context.getNormalEmbed();
            eb.setAuthor(context.getTranslated("help.header.title"), null, context.getEvent().getJDA().getSelfUser().getAvatarUrl());
            HashMap<CommandCategory, List<String>> categories = new HashMap<>();
            for (CommandCategory c : CommandCategory.values()) categories.put(c, new ArrayList<>());

            commandManager.getRegistered().forEach(command -> {
                if (command.getCategory() != null) {
                    categories.get(command.getCategory()).add(command.getName());
                }
            });

            categories.forEach((category, commands) -> {
                if (commands.size() != 0)
                    eb.addField(context.getTranslated("help.category." + category.name().toLowerCase()) + " - (" + commands.size() + ")", "`" + Joiner.on("`, `").join(commands) + "`", false);
            });

            context.send(eb.build());
        }
    }
}
