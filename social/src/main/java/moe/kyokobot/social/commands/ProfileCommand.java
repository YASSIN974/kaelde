package moe.kyokobot.social.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.CommonUtil;
import moe.kyokobot.bot.util.UserUtil;
import moe.kyokobot.social.requester.ImageRequester;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProfileCommand extends Command {
    private final ImageRequester imageRequester;
    private HashMap<Guild, Long> cooldowns;

    public ProfileCommand(ImageRequester imageRequester) {
        this.imageRequester = imageRequester;
        this.name = "profile";
        this.category = CommandCategory.SOCIAL;
        this.description = "profile.description";
        this.usage = "generic.useronlyusage";
        this.cooldowns = new HashMap<>();
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        if (CommonUtil.checkCooldown(cooldowns, context, 5000)) return;

        context.getChannel().sendMessage(CommandIcons.WORKING + context.getTranslated("generic.loading")).queue(message -> {
            try {
                byte[] image;
                if (context.hasArgs()) {
                    Member member = UserUtil.getMember(context.getGuild(), context.getConcatArgs(), true);
                    if (member == null) {
                        CommonErrors.editNoUserFound(context, context.getConcatArgs(), message);
                        return;
                    } else {
                        image = imageRequester.getProfile(member.getUser());
                    }
                } else {
                    image = imageRequester.getProfile(context.getSender());
                }
                context.getChannel().sendFile(image, "profile.png").queue(success -> message.delete().queue(), error -> {
                    error.printStackTrace();
                    Sentry.capture(error);
                    CommonErrors.editException(context, error, message);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }
}
