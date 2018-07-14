package moe.kyokobot.nsfw.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.GsonUtil;

import static moe.kyokobot.bot.util.NetworkUtil.download;

public class TrapCommand extends NsfwCommand {
    public TrapCommand() {
        name = "trap";
        usage = "";
    }

    @Override
    public void execute(CommandContext context) {
        context.send(CommandIcons.WORKING + context.getTranslated("generic.loading"), message -> {
            try {
                String data = new String(download("https://api.computerfreaker.cf/v1/trap"));
                TrapResponse response = GsonUtil.fromJSON(data, TrapResponse.class);
                if (response.url == null || response.url.isEmpty()) {
                    message.editMessage(CommandIcons.ERROR + context.getTranslated("api.computerfreaker.error")).queue();
                } else {
                    EmbedBuilder eb = context.getNormalEmbed();
                    eb.setTitle(context.getTranslated("weebsh.description.trap"));
                    eb.setFooter(Constants.POWERED_BY_CF, null);
                    eb.setImage(response.url);
                    message.editMessage(eb.build()).override(true).queue();
                }
            } catch (Exception e) {
                logger.error("Error while querying CF's API!", e);
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }

    private class TrapResponse {
        private String url;
    }
}
