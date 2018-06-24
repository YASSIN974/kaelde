package moe.kyokobot.social.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;

public class RichestCommand extends Command {
    private final DatabaseManager databaseManager;

    public RichestCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;

        name = "richest";
        aliases = new String[] {"topmoney", "topcash"};
        category = CommandCategory.SOCIAL;
    }

    @Override
    public void execute(CommandContext context) {
        //context.send(r.table("users").orderBy("money").limit(10).toJsonString().run());
    }
}
