package me.gabixdev.kyoko.command;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.command.basic.AbstractEmbedBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandContext;
import me.gabixdev.kyoko.util.command.CommandInfo;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class BasicCommands {

    private final Kyoko kyoko;

    public BasicCommands(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @CommandInfo(name = "help", description = "Prints all bot commands!")
    public void helpCommand(Message message, CommandContext context) {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        StringBuilder labels = new StringBuilder();
        StringBuilder descs = new StringBuilder();
        for (Command command : kyoko.getCommands().getCommands()) {
            labels.append("!").append(command.getCommand()).append("\n");
            descs.append(command.getDescription()).append("\n");
        }
        String ls = labels.toString();
        String ds = descs.toString();
        normal.addField("Command", ls.substring(0, ls.length() - 1), true);
        normal.addField("Description", ds.substring(0, ds.length() - 1), true);
        message.getChannel().sendMessage(normal.build()).queue();
    }

    @CommandInfo(name = "author", description = "Shows bot autors!")
    public void authorCommand(Message message, CommandContext context) {
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        eb.addField("Kyoko version", Constants.VERSION, true);
        eb.addField("Authors", Constants.GITHUB_URL + "contributors", true);
        eb.addField("Official Discord server", Constants.DISCORD_URL, true);

        message.getChannel().sendMessage(eb.build()).queue();
    }

    @CommandInfo(name = "invite", description = "Invite Kyoko to your server!")
    public void inviteCommand(Message message, CommandContext context) {
        String clientId = kyoko.getJda().getSelfUser().getId();
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        eb.addField("Invite URL", new StringBuilder().append("https://discordapp.com/oauth2/authorize?&client_id=").append(clientId).append("&scope=bot&permissions=1207434304").toString(), true);

        message.getChannel().sendMessage(eb.build()).queue();
    }

    @CommandInfo(name = "statistics", description = "Shows bot statistics!")
    public void statisticsCommand(Message message, CommandContext context) {
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        eb.addField("Servers", String.valueOf(kyoko.getJda().getGuilds().size()), true);
        eb.addField("Clients", String.valueOf(kyoko.getJda().getUsers().size()), true);
        message.getChannel().sendMessage(eb.build()).queue();
    }

}
