package moe.kyokobot.misccommands.commands;

import com.google.common.base.Joiner;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Role;

import java.util.stream.Collectors;

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
        eb.setTitle(context.getTranslated("userinfo.user"));
        eb.addField(context.getTranslated("userinfo.tag"), member.getUser().getName() + "#" + member.getUser().getDiscriminator(), false);
        eb.addField(context.getTranslated("userinfo.id"), member.getUser().getId(), false);
        eb.addField(context.getTranslated("userinfo.status"), member.getOnlineStatus().name(), false);
        eb.addField(context.getTranslated("userinfo.game"), member.getGame() == null ? context.getTranslated("generic.none") : gameToString(member.getGame()), false);
        eb.addField(context.getTranslated("userinfo.roles"), member.getRoles().size() == 0 ? context.getTranslated("generic.none") : "`" + member.getRoles().stream().map(Role::getName).collect(Collectors.joining("`, `")) + "`", false);
        context.send(eb.build());
    }

    private String gameToString(Game game) {
        if (game.isRich()) {
            return game.asRichPresence().toString();
        }
        return game.getName();
    }

    private String prettyStatus(CommandContext context, OnlineStatus status) {
        String s = "";
        if (context.getEvent().getJDA().getGuildById("110373943822540800") != null) { // Discord Bots > DBL
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
    }
}