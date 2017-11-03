package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class InviteCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"invite"};

    public InviteCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "invite.description";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        String clientId = kyoko.getJda().getSelfUser().getId();
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        eb.addField(kyoko.getI18n().get(l, "invite.msg.inviteurl"), new StringBuilder().append("https://discordapp.com/oauth2/authorize?&client_id=").append(clientId).append("&scope=bot&permissions=" + Constants.PERMISSIONS).toString(), false);
        eb.addField(kyoko.getI18n().get(l, "invite.msg.discordurl"), Constants.DISCORD_URL, false);
        eb.addField(kyoko.getI18n().get(l, "invite.msg.plsvote"), Constants.DISCORDBOTS_URL, false);
        eb.addField(kyoko.getI18n().get(l, "invite.msg.givmestar"), Constants.GITHUB_URL, false);
        message.getChannel().sendMessage(eb.build()).queue();
    }
}
