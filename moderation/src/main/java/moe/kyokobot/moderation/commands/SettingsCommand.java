package moe.kyokobot.moderation.commands;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.moderation.menu.SettingsMenu;
import net.dv8tion.jda.core.Permission;

public class SettingsCommand extends Command {

    private final EventWaiter eventWaiter;
    private final DatabaseManager databaseManager;

    public SettingsCommand(EventWaiter eventWaiter, DatabaseManager databaseManager) {
        name = "settings";
        category = CommandCategory.MODERATION;

        this.eventWaiter = eventWaiter;
        this.databaseManager = databaseManager;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getMember().hasPermission(Permission.MANAGE_SERVER)
                || (Constants.BOTLIST_GUILDS.contains(context.getGuild().getId()) // allow owner to configure bot in botlist guilds
                && context.getSettings().bot.owner.equals(context.getSender().getId()))) {
            new SettingsMenu(eventWaiter, databaseManager, context).create();
        } else {
            CommonErrors.noPermissionUser(context);
        }
    }
}
