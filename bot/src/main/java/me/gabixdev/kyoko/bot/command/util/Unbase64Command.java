package me.gabixdev.kyoko.bot.command.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.CommonErrors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Base64;

public class Unbase64Command extends Command {
    private final Kyoko kyoko;

    public Unbase64Command(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "unbase64";
        this.aliases = new String[]{"debase64"};
        this.category = CommandCategory.UTILITY;
        this.description = "unbase64.description";
        this.usage = "unbase64.usage";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            CommonErrors.usage(context);
            return;
        }

        try {
            byte[] data = Base64.getDecoder().decode(context.getConcatArgs());
            String decoded = new String(data, "UTF-8");

            if (decoded.length() > 2000) {
                EmbedBuilder eb = context.getErrorEmbed();
                eb.addField(context.getTranslated("generic.error"), context.getTranslated("generic.toolong"), false);
                context.send(eb.build());
            } else {
                context.send(decoded);
            }
        } catch (Exception e) {
            CommonErrors.exception(context);
            e.printStackTrace();
        }
    }
}
