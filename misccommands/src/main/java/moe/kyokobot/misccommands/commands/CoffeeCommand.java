package moe.kyokobot.misccommands.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.GsonUtil;
import net.dv8tion.jda.core.EmbedBuilder;

import static moe.kyokobot.bot.util.NetworkUtil.download;

public class CoffeeCommand extends Command {
    public CoffeeCommand() {
        name = "coffee";
        description = "coffee.description";
        category = CommandCategory.IMAGES;
    }

    @Override
    public void execute(CommandContext context) {
        context.send(context.working() + context.getTranslated("generic.loading"), message -> {
            try {
                String data = new String(download("https://coffee.alexflipnote.xyz/random.json"));
                CoffeeResponse response = GsonUtil.gson.fromJson(data, CoffeeResponse.class);
                if (response.file == null || response.file.isEmpty()) {
                    message.editMessage(context.error() + context.getTranslated("api.coffee.error")).queue();
                } else {
                    EmbedBuilder eb = context.getNormalEmbed();
                    eb.addField(context.getTranslated("coffee.title"), Constants.POWERED_BY_ALEX, false);
                    eb.setImage(response.file);
                    message.editMessage(eb.build()).override(true).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }

    private class CoffeeResponse {
        private String file;
    }
}
