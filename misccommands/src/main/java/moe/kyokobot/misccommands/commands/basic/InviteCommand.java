package moe.kyokobot.misccommands.commands.basic;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.EmbedBuilder;

public class InviteCommand extends Command {
    public InviteCommand() {
        name = "invite";
        category = CommandCategory.BASIC;
        usage = "";
    }

    @Override
    public void execute(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();
        eb.setTitle(context.getTranslated("invite.title"));
        eb.addField(context.getTranslated("invite.link"),
                "https://discordapp.com/oauth2/authorize?&client_id=375750637540868107&scope=bot&permissions=" + Constants.PERMISSIONS, false);
        eb.addField(context.getTranslated("invite.support"), Constants.DISCORD_URL, false);
        eb.addField(context.getTranslated("invite.vote"), Constants.DISCORDBOTS_URL, false);
        context.send(eb.build());
    }
}
