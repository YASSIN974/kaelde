package me.gabixdev.kyoko.bot.command.normal.basic;

import me.gabixdev.kyoko.bot.Constants;
import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import net.dv8tion.jda.core.EmbedBuilder;

public class InviteCommand extends Command {
    private final Kyoko kyoko;

    public InviteCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "invite";
        this.category = CommandCategory.BASIC;
        this.description = "invite.description";
        this.usage = null;
    }

    @Override
    public void execute(CommandContext context) {
        String clientId = kyoko.getJda().getSelfUser().getId();
        EmbedBuilder eb = context.getNormalEmbed();

        eb.addField(context.getTranslated("invite.msg.inviteurl"), new StringBuilder().append("https://discordapp.com/oauth2/authorize?&client_id=").append(clientId).append("&scope=bot&permissions=" + Constants.PERMISSIONS).toString(), false);
        eb.addField(context.getTranslated("invite.msg.discordurl"), Constants.DISCORD_URL, false);
        eb.addField(context.getTranslated("invite.msg.plsvote"), Constants.DISCORDBOTSORG_URL, false);
        //eb.addField(context.getTranslated("invite.msg.plsvotebdp"), Constants.BOTSDISCORDPW_URL, false); // no voting
        eb.addField(context.getTranslated("invite.msg.givmestar"), Constants.GITHUB_URL, false);
        context.send(eb.build());
    }
}
