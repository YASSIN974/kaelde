package me.gabixdev.kyoko.bot.command.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.CommonErrors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Base64;

public class Base64Command extends Command {
    private final Kyoko kyoko;

    public Base64Command(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "base64";
        this.category = CommandCategory.UTILITY;
        this.description = "base64.description";
        this.usage = "base64.usage";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            CommonErrors.usage(context);
            return;
        }

        try {
            String based = Base64.getEncoder().encodeToString(context.getConcatArgs().getBytes("UTF-8"));
            if (based.length() > 2000) {
                EmbedBuilder eb = context.getErrorEmbed();
                eb.addField(context.getTranslated("generic.error"), context.getTranslated("generic.toolong"), false);
                context.send(eb.build());
            } else {
                context.send(based);
            }
        } catch (Exception e) {
            CommonErrors.exception(context);
            e.printStackTrace();
        }
    }
}
