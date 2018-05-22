package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerInfoCommand extends Command {
    private CommandManager commandManager;

    public ServerInfoCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        name = "serverinfo";
        category = CommandCategory.UTILITY;
    }

    @Override
    public void execute(CommandContext context) {
    Guild guild = context.getGuild();
            sendGuild(context, guild);
    }

    private void sendGuild(CommandContext context, Guild guild) {
        EmbedBuilder eb = context.getNormalEmbed();
        eb.setThumbnail(guild.getIconUrl());
        eb.setTitle(context.getTranslated("server.title"));
        eb.addField(context.getTranslated("server.title.header"), guild.getName(), true);
        eb.addField(context.getTranslated("server.members"), String.format("%s", guild.getMembers().size()), true);
        eb.addField(context.getTranslated("server.channels"), String.format("%s text channels\n%s voice channels", guild.getTextChannels().size(), guild.getVoiceChannels().size()), true);
        eb.addField(context.getTranslated("server.verification"), String.format("%s", guild.getVerificationLevel()), true);
        eb.addField(context.getTranslated("server.region"), String.format("%s", guild.getRegion()), true);
        eb.addField(context.getTranslated("server.owner"),  String.format("%s", guild.getOwner()), true);
        eb.addField(context.getTranslated("server.created"), new SimpleDateFormat("dd MMMM yyyy").format(new Date(guild.getCreationTime().toInstant().toEpochMilli())), true);
        eb.addField(context.getTranslated("ID:"), String.format("%s", guild.getId()), true);
        context.send(eb.build());
    }
}
