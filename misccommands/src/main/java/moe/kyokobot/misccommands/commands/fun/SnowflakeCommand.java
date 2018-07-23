package moe.kyokobot.misccommands.commands.fun;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import net.dv8tion.jda.core.utils.MiscUtil;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;

import static net.dv8tion.jda.core.utils.MiscUtil.DISCORD_EPOCH;
import static net.dv8tion.jda.core.utils.MiscUtil.TIMESTAMP_OFFSET;

public class SnowflakeCommand extends Command {
    public SnowflakeCommand() {
        name = "snowflake";
        category = CommandCategory.FUN;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        try {
            if (!context.hasArgs()) {
                CommonErrors.usage(context);
                return;
            }

            long snowflake = MiscUtil.parseSnowflake(context.getConcatArgs());

            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(TimeZone.getTimeZone("UTC").toZoneId()).withLocale(context.getLanguage().getLocale());
            String timestamp = MiscUtil.getCreationTime(snowflake).format(formatter);
            String unix = String.valueOf((snowflake >>> TIMESTAMP_OFFSET) + DISCORD_EPOCH);
            String workerID = String.valueOf((snowflake & 0x3E0000) >>> 17);
            String processID = String.valueOf((snowflake & 0x1F000) >>> 12);
            String increment = String.valueOf(snowflake & 0xFFF);

            EmbedBuilder eb = context.getNormalEmbed();
            eb.setTitle(context.getTranslated("snowflake.title"));
            eb.setDescription(String.format(context.getTranslated("snowflake.out"), timestamp, unix, workerID, processID, increment));
            context.send(eb.build());
        } catch (NumberFormatException e) {
            context.send(CommandIcons.ERROR + context.getTranslated("snowflake.invalid"));
        }
    }
}
