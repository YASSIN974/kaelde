package moe.kyokobot.misccommands.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

public class AvatarCommand extends Command {
    private CommandManager commandManager;

    public AvatarCommand(CommandManager commandManager) {
        this.commandManager = commandManager;

        name = "avatar";
        category = CommandCategory.UTILITY;
        description = "avatar.description";
    }


    @Override
    public void execute(CommandContext context) {
        Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
        EmbedBuilder eb = context.getNormalEmbed();
        if (context.hasArgs()) {
            if (member != null) {
                if (member.getUser().getAvatarUrl() == null) {
                    context.send(context.error() + context.getTranslated("avatar.null"));
                }
                eb.addField(context.getTranslated("avatar.user") + member.getUser().getName() + "#" + member.getUser().getDiscriminator(), "", false);
                eb.setImage(member.getUser().getAvatarUrl());
                context.send(eb.build());
            } else {
                context.send(context.error() + "" + context.getTranslated("avatar.usernotfound"));
            }
        } else {
            context.send(context.error() + context.getTranslated("avatar.error"));
        }
    }
}