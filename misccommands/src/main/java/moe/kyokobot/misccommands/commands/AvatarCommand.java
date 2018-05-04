package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.UserUtil;
import moe.kyokobot.bot.util.NetworkUtil;
import java.io.IOException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;

public class AvatarCommand extends Command {
    private CommandManager commandManager;

    public AvatarCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        name="avatar";
        category=CommandCategory.UTILITY;
        description="avatar.description";
    }


    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 0) {
            context.send(context.error() + "" + context.getTranslated("avatar.error"));
            return;
        }

        Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
        if (member != null) {
            if (member.getUser().getAvatarUrl() == null) {
                EmbedBuilder eb = context.getErrorEmbed();
                eb.addField(context.getTranslated("generic.error"), context.getTranslated("avatar.null"), false);
                context.send(eb.build());
            } else {
                try {
                    byte[] data = NetworkUtil.download(member.getUser().getAvatarUrl());
                    context.getChannel().sendFile(data, "avatar.gif", new MessageBuilder().append("Avatar: **" + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + "**").build()).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                    context.error();
                }
            }
        } else {
            context.send(context.error() + "" + context.getTranslated("avatar.usernotfound"));
        }
    }
}