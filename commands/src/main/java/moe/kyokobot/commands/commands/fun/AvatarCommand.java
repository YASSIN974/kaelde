package moe.kyokobot.commands.commands.fun;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

public class AvatarCommand extends Command {

    public AvatarCommand() {
        name = "avatar";
        usage = "generic.useronlyusage";
        category = CommandCategory.FUN;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.hasArgs()) {
            Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
            if (member != null) {
                printAvatar(context, member.getUser());
            } else {
                CommonErrors.noUserFound(context, context.getConcatArgs());
            }
        } else {
            printAvatar(context, context.getSender());
        }
    }

    private void printAvatar(CommandContext context, User user) {
        EmbedBuilder eb = context.getNormalEmbed();
        eb.addField(String.format(context.getTranslated("avatar.user"), user.getName() + "#" + user.getDiscriminator()), String.format("[%s](%s)", context.getTranslated("avatar.direct"), user.getEffectiveAvatarUrl()), false);
        eb.setImage(user.getEffectiveAvatarUrl() + "?size=1024");
        context.send(eb.build());
    }
}