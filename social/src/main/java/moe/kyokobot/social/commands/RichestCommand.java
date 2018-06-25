package moe.kyokobot.social.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.EmbedBuilder;

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
        EmbedBuilder eb = context.getNormalEmbed();
        eb.setTitle("top 10 balances");

    }
}
