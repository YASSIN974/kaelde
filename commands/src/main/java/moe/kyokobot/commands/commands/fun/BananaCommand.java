package moe.kyokobot.commands.commands.fun;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.UserUtil;
import net.dv8tion.jda.core.entities.Member;
import org.jetbrains.annotations.NotNull;

public class BananaCommand extends Command {
    public BananaCommand() {
        this.name = "banana";
        this.aliases = new String[]{"banan", "mujbanan", "tvujbanan"};
        this.category = CommandCategory.FUN;
        this.usage = "generic.useronlyusage";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();

        if (!context.getConcatArgs().isEmpty()) {
            Member m = UserUtil.getMember(context.getGuild(), context.getConcatArgs());
            short length = (short) (m == null ? (9 + context.getConcatArgs().hashCode() % 20) : (9 + m.getUser().getIdLong() % 20));
            String name = m == null ? context.getConcatArgs() : m.getEffectiveName();
            eb.setTitle(name + " má banán dlouhý " + Math.abs(length) + " cm.");
        } else
            eb.setTitle("Tvůj banán má " +  Math.abs(9 + context.getSender().getIdLong() % 20) + " cm.");

        eb.setImage("https://upload.wikimedia.org/wikipedia/commons/thumb/3/32/Twemoji2_1f34c.svg/512px-Twemoji2_1f34c.svg.png");
        context.send(eb.build());
    }
}
