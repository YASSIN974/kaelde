package moe.kyokobot.misccommands.commands.fun;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.NetworkUtil;
import moe.kyokobot.bot.util.EmbedBuilder;

public class InspireCommand extends Command {

    public InspireCommand() {
        name = "inspire";
        category = CommandCategory.FUN;
        usage = "";
    }

    @Override
    public void execute(CommandContext context) {
        context.send(CommandIcons.working + context.getTranslated("generic.loading"), message -> {
            try {
                String url = new String(NetworkUtil.download("http://inspirobot.me/api?generate=true"));
                EmbedBuilder eb = context.getNormalEmbed();
                eb.setTitle("InspiroBot");
                eb.setFooter("Powered by inspirobot.me", null);
                eb.setImage(url);
                message.editMessage(eb.build()).override(true).queue();
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
                CommonErrors.editException(context, e, message);
            }
        });
    }
}
