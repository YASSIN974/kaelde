package moe.kyokobot.misccommands.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.GsonUtil;

import static moe.kyokobot.bot.util.NetworkUtil.download;

public class WhyCommand extends Command {
    public WhyCommand() {
        name = "why";
        description = "why.description";
        aliases = new String[] {"huh", "hmm"};
        category = CommandCategory.FUN;
    }


    @Override
    public void execute(CommandContext context) {
        context.send(context.working() + context.getTranslated("generic.loading"), message -> {
            try {
                String data = new String(download("https://nekos.life/api/v2/why"));
                NekosResponse response = GsonUtil.gson.fromJson(data, NekosResponse.class);
                if (response.why == null || response.why.isEmpty()) {
                    message.editMessage(context.error() + context.getTranslated("api.nekoslife.error")).queue();
                } else {
                    message.editMessage(response.why).override(true).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }

    private class NekosResponse {
        private String why;
    }
}
