package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.CommonErrors;
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
        if (context.hasArgs()) {
            Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
            if (member != null) {
                sendProfile(context, member);
            } else {
                CommonErrors.noUserFound(context, context.getConcatArgs());
            }
        } else {
            sendProfile(context, context.getMember());
        }
    }

    private void sendProfile(CommandContext context, Member member) {
        EmbedBuilder eb = context.getNormalEmbed();
        eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        eb.addField(context.getTranslated("userinfo.user") + member.getUser().getName() + "#" + member.getUser().getDiscriminator(), "", false);
        eb.addField("ID: ", member.getUser().getId() + "", false);
        eb.addField("Status:", member.getOnlineStatus() + "", false);
        eb.addField(context.getTranslated("userinfo.game"), member.getGame() + "", false);
        eb.addField(context.getTranslated("userinfo.roles"), member.getRoles() + "", false);
        context.send(eb.build());
    }
}