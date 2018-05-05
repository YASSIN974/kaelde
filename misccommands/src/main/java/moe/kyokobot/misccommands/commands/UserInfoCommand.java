package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import moe.kyokobot.bot.util.UserUtil;

public class UserInfoCommand extends Command {
    private CommandManager commandManager;

    public UserInfoCommand(CommandManager commandManager) {
        this.commandManager = commandManager;


        name = "userinfo";
        category = CommandCategory.UTILITY;
        description = "userinfo.description";
    }


    @Override
    public void execute(CommandContext context) {
        Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
        EmbedBuilder eb = context.getNormalEmbed();
        if (context.hasArgs()) {
            if (member != null) {
                eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
                eb.addField(context.getTranslated("userinfo.user") + member.getUser().getName() + "#" + member.getUser().getDiscriminator(), "", false);
                eb.addField("ID: ", member.getUser().getId() + "", false);
                eb.addField("Status:", member.getOnlineStatus() + "", false);
                eb.addField(context.getTranslated("userinfo.game"), member.getGame() + "", false);
                eb.addField(context.getTranslated("userinfo.roles"), member.getRoles() + "", false);
                context.send(eb.build());
            } else {
                context.send(context.error() + context.getTranslated("userinfo.notfound"));
            }
        } else {
            eb.setThumbnail(context.getSender().getEffectiveAvatarUrl());
            eb.addField(context.getTranslated("userinfo.user") + context.getSender().getName() + "#" + context.getSender().getDiscriminator(), "", false);
            eb.addField("ID: ", context.getSender().getId() + "", false);
            eb.addField("Status:", context.getMember().getOnlineStatus() + "", false);
            eb.addField(context.getTranslated("userinfo.game"), context.getMember().getGame() + "", false);
            eb.addField(context.getTranslated("userinfo.roles"), context.getMember().getRoles() + "", false);
            context.send(eb.build());
        }
    }
}