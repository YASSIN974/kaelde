package moe.kyokobot.social.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;

public class SendMoneyCommand extends Command {
    private final DatabaseManager databaseManager;

    public SendMoneyCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(CommandContext context) {

    }
}
