package moe.kyokobot.bot.command.debug;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import moe.kyokobot.bot.command.*;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.StringUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class GenDocsCommand extends Command {
    private final CommandManager commandManager;

    public GenDocsCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        name = "gendocs";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        StringBuilder sb = new StringBuilder();

        TreeMap<CommandCategory, List<Command>> categories = Arrays.stream(CommandCategory.values()).collect(Collectors.toMap(c -> c, c -> new ArrayList<>(), (a, b) -> b, TreeMap::new));
        commandManager.getRegistered().stream().filter(command -> command.getCategory() != null).forEach(command -> categories.get(command.getCategory()).add(command));

        categories.forEach((category, commands) -> {
            if (commands.isEmpty()) return;
            sb.append("<h2>").append(context.getTranslated("help.category." + category.name().toLowerCase())).append("</h2>\n\n");
            sb.append("\t<div class=\"table-responsive-md\">\n" +
                    "\t\t<table class=\"table table-dark\">\n" +
                    "\t\t\t<thead>\n" +
                    "\t\t\t\t<tr>\n" +
                    "\t\t\t\t\t<th style=\"width: 25%\" scope=\"col\">Command</th>\n" +
                    "\t\t\t\t\t<th style=\"width: 25%\" scope=\"col\">Aliases</th>\n" +
                    "\t\t\t\t\t<th style=\"width: 25%\" scope=\"col\">Description</th>\n" +
                    "\t\t\t\t\t<th style=\"width: 25%\" scope=\"col\">Usage</th>\n" +
                    "\t\t\t\t</tr>\n" +
                    "\t\t\t</thead>\n" +
                    "\t\t\t<tbody>\n");
            commands.forEach(cmd -> {
                sb.append("\t\t\t\t<tr>\n\t\t\t\t\t<td>")
                        .append(cmd.getName()).append("</td>\n\t\t\t\t\t<td>")
                        .append(cmd.getAliases() == null || cmd.getAliases().length == 0 ? "(none)" :
                                Joiner.on(", ").join(cmd.getAliases())).append("</td>\n\t\t\t\t\t<td>")
                        .append(context.getTranslated(cmd.getDescription()).replaceAll("(\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", "<a href=\"$1\">$1</a>")).append("</td>\n\t\t\t\t\t<td><code>")
                        .append("ky!").append(cmd.getName()).append(" ").append(context.getTranslated(cmd.getUsage())).append("</code></td>\n\t\t\t\t</tr>\n");
            });
            sb.append("\t\t\t</tbody>\n" +
                    "\t\t</table>\n" +
                    "\t</div>\n");
        });

        Path file = Paths.get("docs_generated_" + StringUtil.prettyPeriod(System.currentTimeMillis()).replace(":", "") + ".html");

        try {
            Files.write(file, sb.toString().getBytes(Charsets.UTF_8), StandardOpenOption.CREATE_NEW);
            context.send(CommandIcons.SUCCESS + "Docs generated! `" + file.toFile().getName() + "`");
        } catch (IOException e) {
            context.send(CommandIcons.ERROR + "Error writing file: `" + e.getMessage() + "`");
            logger.error("Error writing file!", e);
        }
    }
}
