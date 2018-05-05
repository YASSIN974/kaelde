package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import moe.kyokobot.bot.util.UserUtil;

public class userinfoCommand extends Command {
    private CommandManager commandManager;

    public userinfoCommand(CommandManager commandManager) {
        this.commandManager = commandManager;


        name = "userinfo";
        category = CommandCategory.UTILITY;
        description = "userinfo.description";
    }


    @Override
    public void execute(CommandContext context) {
        Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
        EmbedBuilder eb = context.getNormalEmbed();
        if (context.getArgs().length == 0) {
            context.send(context.error() + context.getTranslated("userinfo.error"));
            return;
        }
            if (member != null) {
            eb.setThumbnail(member.getUser().getAvatarUrl());
            eb.addField("User " + member.getUser().getName() + "#" + member.getUser().getDiscriminator(), "", false);
            eb.addField("ID: ", member.getUser().getId() + "", false);
            eb.addField("Status:", member.getOnlineStatus() + "", false);
            eb.addField(context.getTranslated("userinfo.game"), member.getGame() + "", false);
            eb.addField(context.getTranslated("userinfo.roles"), member.getRoles() + "", false);
            context.send(eb.build());
        } else {
                context.send(context.error() + context.getTranslated("avatar.usernotfound"));

        }
    }
}