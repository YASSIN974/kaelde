package moe.kyokobot.social.commands;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.u8.U8_Grids;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RichestCommand extends Command {
    private final DatabaseManager databaseManager;

    public RichestCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;

        name = "richest";
        usage = "";
        aliases = new String[] {"topmoney", "topcash"};
        category = CommandCategory.SOCIAL;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        Map<String, Integer> tops = databaseManager.getTopBalances();
        int i = 0;
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow(context.getTranslated("richest.title"));
        at.addRule();
        for (Map.Entry<String, Integer> e : tops.entrySet()) {
            i++;

            // TODO name cache?
            User u = context.getEvent().getJDA().getUserById(e.getKey());
            if (u == null)
                at.addRow(i + ". [" + "unknown user" + "] - " + e.getValue() + "$");
            else
                at.addRow(i + ". " + UserUtil.toDiscrim(u) + " - " + e.getValue() + "$");
        }
        at.addRule();
        at.getContext().setGrid(U8_Grids.borderDouble());

        context.send("```less\n" + at.render(50) + "\n```");
    }
}
