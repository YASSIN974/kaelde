package me.gabixdev.kyoko.bot.command.normal.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.CommonErrors;
import me.gabixdev.kyoko.bot.util.URLUtil;
import me.gabixdev.kyoko.bot.util.UserUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;

public class AvatarCommand extends Command {
    private final Kyoko kyoko;

    public AvatarCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "avatar";
        this.category = CommandCategory.UTILITY;
        this.description = "avatar.description";
        this.usage = "avatar.usage";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            CommonErrors.usage(context);
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
                    byte[] data = URLUtil.readUrlBytes(member.getUser().getAvatarUrl());
                    context.getChannel().sendFile(data, "avatar.png", new MessageBuilder().append(String.format(context.getTranslated("avatar.user"), member.getEffectiveName())).build()).queue();
                } catch (IOException e) {
                    e.printStackTrace();
                    CommonErrors.exception(context);
                }
            }
        } else {
            CommonErrors.noUserFound(context, context.getConcatArgs());
        }
    }
}
