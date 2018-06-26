package moe.kyokobot.nsfw.commands;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.GsonUtil;
import moe.kyokobot.bot.util.RandomUtil;

import static moe.kyokobot.bot.util.NetworkUtil.download;

public class BoobsCommand extends NsfwCommand {
    private final String[] BOOBIES = new String[] {"( . Y . )", "( .)(. )", "( • ) ( • )ԅ(‾⌣‾ԅ)", "( • )( • )ԅ(≖⌣≖ԅ)", "（(◎)＿(◎)）"};
    public BoobsCommand() {
        name = "boobs";
        aliases = new String[] {"boobies", "tits"};
        description = "boobs.description";
    }

    @Override
    public void execute(CommandContext context) {
        context.send(CommandIcons.WORKING + context.getTranslated("generic.loading"), message -> {
            try {
                String data = new String(download("http://api.oboobs.ru/boobs/0/1/random"));
                OBoobsResponse[] responses = GsonUtil.fromJSON(data, OBoobsResponse[].class);
                OBoobsResponse response = responses[0];
                if (response.id == -1) {
                    message.editMessage(CommandIcons.ERROR + context.getTranslated("api.oboobs.error")).queue();
                } else {
                    EmbedBuilder eb = context.getNormalEmbed();
                    eb.setTitle(RandomUtil.randomElement(BOOBIES), "http://oboobs.ru/b/" + response.id);
                    eb.setDescription("ID: " + response.id + " Rank: " + response.rank);
                    eb.setImage("http://media.oboobs.ru/" + response.preview);
                    message.editMessage(eb.build()).override(true).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }

    private class OBoobsResponse {
        private int id = -1;
        private int rank = 0;
        private String preview = "";
    }
}
