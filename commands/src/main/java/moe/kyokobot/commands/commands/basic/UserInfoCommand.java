package moe.kyokobot.commands.commands.basic;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class UserInfoCommand extends Command {
    public UserInfoCommand() {
        name = "userinfo";
        usage = "generic.useronlyusage";
        category = CommandCategory.BASIC;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(context.getLanguage().getLocale());
        EmbedBuilder eb = context.getNormalEmbed();
        eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        eb.setTitle(context.getTranslated("userinfo.user"));
        eb.addField(context.getTranslated("userinfo.tag"), member.getUser().getName() + "#" + member.getUser().getDiscriminator(), true);
        eb.addField(context.getTranslated("userinfo.id"), member.getUser().getId(), true);
        eb.addField(context.getTranslated("userinfo.status"), prettyStatus(context, member.getOnlineStatus()), true);
        eb.addField(context.getTranslated("userinfo.game"), member.getGame() == null ? context.getTranslated("generic.none") : gameToString(member.getGame()), true);
        eb.addField(context.getTranslated("userinfo.joindate"), member.getJoinDate().format(formatter),true);
        eb.addField(context.getTranslated("userinfo.roles"), getRoles(context, member), true);
        context.send(eb.build());
    }

    private String gameToString(Game game) {
        if (game.isRich()) {
            return game.asRichPresence().getName() + "\n" + game.asRichPresence().getDetails();
        }
        return game.getName();
    }

    private String getRoles(CommandContext context, Member member) {
        if (member.getRoles().isEmpty()) {
            return context.getTranslated("generic.none");
        } else {
            return member.getRoles().stream().map(Role::getName).collect(StringUtil.limitingJoin(", ", 128, " and more"));
        }

    }

    private String prettyStatus(CommandContext context, OnlineStatus status) {
        String s = "";
        if (context.getEvent().getJDA().getGuildById("110373943822540800") != null) {
            switch (status) {
                case ONLINE:
                    s = "<:online:313956277808005120>";
                    break;
                case IDLE:
                    s = "<:away:313956277220802560>";
                    break;
                case DO_NOT_DISTURB:
                    s = "<:dnd:313956276893646850>";
                    break;
                case INVISIBLE:
                case OFFLINE:
                case UNKNOWN:
                    s = "<:offline:313956277237710868>";
                    break;
            }
        }
        s += status.name();
        return s;
    }
}