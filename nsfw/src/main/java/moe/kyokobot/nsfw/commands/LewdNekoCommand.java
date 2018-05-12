package moe.kyokobot.nsfw.commands;

import com.google.gson.Gson;
import io.sentry.Sentry;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.GsonUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

import static moe.kyokobot.bot.util.NetworkUtil.download;

public class LewdNekoCommand extends NsfwCommand {
    public LewdNekoCommand() {
        name = "lewdneko";
        category = CommandCategory.NSFW;
        description = "lewdneko.description";
    }

    @Override
    public void execute(CommandContext context) {
        context.send(context.working() + context.getTranslated("generic.loading"), message -> {
            try {
                String data = new String(download("https://nekos.life/api/v2/img/lewd"));
                NekosResponse response = GsonUtil.gson.fromJson(data, NekosResponse.class);
                if (response.url == null || response.url.isEmpty()) {
                    message.editMessage(context.error() + context.getTranslated("api.nekoslife.error")).queue();
                } else {
                    EmbedBuilder eb = context.getNormalEmbed();
                    eb.addField(context.getTranslated("lewdneko.title"), Constants.POWERED_BY_NEKOSLIFE, false);
                    eb.setImage(response.url);
                    message.editMessage(eb.build()).override(true).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }

    private class NekosResponse {
        private String url;
    }
}
