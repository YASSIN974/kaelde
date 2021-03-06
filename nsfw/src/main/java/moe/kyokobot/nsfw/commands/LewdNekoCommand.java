package moe.kyokobot.nsfw.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.GsonUtil;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.bot.util.NetworkUtil.download;

public class LewdNekoCommand extends NsfwCommand {
    public LewdNekoCommand() {
        name = "lewdneko";
        usage = "";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        context.send(CommandIcons.WORKING + context.getTranslated("generic.loading"), message -> {
            try {
                String data = new String(download("https://nekos.life/api/v2/img/lewd"));
                NekosResponse response = GsonUtil.fromJSON(data, NekosResponse.class);
                if (response.url == null || response.url.isEmpty()) {
                    message.editMessage(CommandIcons.ERROR + context.getTranslated("api.nekoslife.error")).queue();
                } else {
                    EmbedBuilder eb = context.getNormalEmbed();
                    eb.setTitle(context.getTranslated("lewdneko.title"));
                    eb.setFooter(Constants.POWERED_BY_NEKOSLIFE, null);
                    eb.setImage(response.url);
                    message.editMessage(eb.build()).override(true).queue();
                }
            } catch (Exception e) {
                logger.error("Error while querying nekos.life!", e);
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }

    private class NekosResponse {
        private String url;
    }
}
