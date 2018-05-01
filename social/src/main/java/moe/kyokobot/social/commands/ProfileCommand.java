package moe.kyokobot.social.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.CommonUtil;
import moe.kyokobot.social.requester.ImageRequester;
import net.dv8tion.jda.core.entities.Guild;

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
    public void execute(CommandContext context) {
        if (CommonUtil.checkCooldown(cooldowns, context, 5000)) return;

        context.getChannel().sendMessage(context.working() + context.getTranslated("generic.loading")).queue(message -> {
            try {
                byte[] image = imageRequester.getProfile(context.getSender());
                context.getChannel().sendFile(image, "profile.webp").queue(success -> message.delete().queue(), error -> {
                    error.printStackTrace();
                    CommonErrors.editException(context, error, message);
                });
            } catch (Exception e) {
                e.printStackTrace();
                CommonErrors.editException(context, e, message);
            }
        });
    }
}
