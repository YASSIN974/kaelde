package me.gabixdev.kyoko.bot.command.normal.fun;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.UserUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

public class BananaCommand extends Command {
    private final Kyoko kyoko;

    public BananaCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "banana";
        this.aliases = new String[]{"banan", "mujbanan", "tvujbanan"};
        this.category = CommandCategory.FUN;
        this.description = "banana.description";
        this.usage = "banana.usage";
    }

    @Override
    public void execute(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();

        if (!context.getConcatArgs().isEmpty()) {
            Member m = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
            short length = (short) (m == null ? (9 + context.getConcatArgs().hashCode() % 20) : (9 + m.getUser().getIdLong() % 20));
            String name = m == null ? context.getConcatArgs() : m.getEffectiveName();
            eb.setTitle(String.format(context.getTranslated("banana.else"), name, Math.abs(length)));
        } else
            eb.setTitle(String.format(context.getTranslated("banana.yours"), Math.abs(9 + context.getSender().getIdLong() % 20)));

        eb.setImage("https://gabixdev.me/kyoko/api/banan.png");
        context.send(eb.build());
    }
}
