package com.programmingwizzard.charrizard.bot.command;

import com.programmingwizzard.charrizard.bot.Charrizard;
import com.programmingwizzard.charrizard.bot.command.basic.AbstractEmbedBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import pl.themolka.commons.command.Command;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class BasicCommands extends AbstractEmbedBuilder {

    private final Charrizard charrizard;

    public BasicCommands(Charrizard charrizard) {
        this.charrizard = charrizard;
    }

    @CommandInfo(name = "help", description = "Prints all bot commands.")
    public void helpCommand(Message message, CommandContext context) {
        EmbedBuilder normal = getNormalBuilder();
        StringBuilder labels = new StringBuilder();
        StringBuilder descs = new StringBuilder();
        for (Command command : charrizard.getCommands().getCommands()) {
            labels.append("!").append(command.getCommand()).append("\n");
            descs.append(command.getDescription()).append("\n");
        }
        String ls = labels.toString();
        String ds = descs.toString();
        normal.addField("Command", ls.substring(0, ls.length() - 1), true);
        normal.addField("Description", ds.substring(0, ds.length() - 1), true);
        message.getChannel().sendMessage(normal.build()).queue();
    }

    @CommandInfo(name = "author", description = "Shows bot autors")
    public void authorCommand(Message message, CommandContext context) {
        EmbedBuilder builder = getNormalBuilder()
                                       .addField("Charrizard version", Charrizard.VERSION, true)
                                       .addField("Authors", "https://github.com/CharrizardBot/Charrizard/contributors", true)
                                       .addField("Official Discord server", "https://discord.gg/jBCzCx8", true);
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @CommandInfo(name = "invite", description = "Invite Charrizard for Your server")
    public void author(Message message, CommandContext context) {
        String clientId = charrizard.getJda().getSelfUser().getId();
        EmbedBuilder builder = getNormalBuilder().addField("Invite URL", new StringBuilder().append("https://discordapp.com/oauth2/authorize?&client_id=").append(clientId).append("&scope=bot&permissions=1207434304").toString(), true);
        message.getChannel().sendMessage(builder.build()).queue();
    }

}
