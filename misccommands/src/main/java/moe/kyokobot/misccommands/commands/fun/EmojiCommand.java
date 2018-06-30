package moe.kyokobot.misccommands.commands.fun;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.util.CommonErrors;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.StringUtil;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiCommand extends Command {
    private static final Pattern emojiPatten = Pattern.compile("<:.*:(\\d+)>");
    private static final Pattern animatedEmojiPatten = Pattern.compile("<a:.*:(\\d+)>");

    public EmojiCommand() {
        name = "emoji";
        category = CommandCategory.FUN;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            CommonErrors.usage(context);
            return;
        }

        Matcher staticMatcher = emojiPatten.matcher(context.getConcatArgs());
        Matcher animMatcher = animatedEmojiPatten.matcher(context.getConcatArgs());

        if (staticMatcher.matches()) {
            custom(staticMatcher, context);
        } else if (animMatcher.matches()) {
            animated(animMatcher, context);
        } else {
            unicode(context);
        }
    }

    private void animated(Matcher matcher, CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();
        String id = matcher.replaceFirst("$1");
        String url = "https://cdn.discordapp.com/emojis/" + id + ".gif";
        eb.setThumbnail(url);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(TimeZone.getTimeZone("UTC").toZoneId()).withLocale(context.getLanguage().getLocale());
        String date = MiscUtil.getCreationTime(MiscUtil.parseSnowflake(id)).format(formatter);

        Emote emote = context.getEvent().getJDA().getEmoteById(id);
        if (emote == null) {
            eb.setTitle(context.getTranslated("emote.title"));
            eb.setDescription(String.format(context.getTranslated("emote.custom"), "???", date, id, "???", url));
        } else {
            eb.setTitle(context.getTranslated("emote.title"));
            eb.setDescription(String.format(context.getTranslated("emote.custom"), emote.getName(), date, id, emote.getGuild(), url));
        }
        context.send(eb.build());
    }

    private void custom(Matcher matcher, CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();
        String id = matcher.replaceFirst("$1");
        String url = "https://cdn.discordapp.com/emojis/" + id + ".png";
        eb.setThumbnail(url);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(TimeZone.getTimeZone("UTC").toZoneId()).withLocale(context.getLanguage().getLocale());
        String date = MiscUtil.getCreationTime(MiscUtil.parseSnowflake(id)).format(formatter);

        Emote emote = context.getEvent().getJDA().getEmoteById(id);
        if (emote == null) {
            eb.setTitle(context.getTranslated("emote.title"));
            eb.setDescription(String.format(context.getTranslated("emote.custom"), "???", date, id, "???", url));
        } else {
            eb.setTitle(context.getTranslated("emote.title"));
            eb.setDescription(String.format(context.getTranslated("emote.custom"), emote.getName(), date, id, emote.getGuild(), url));
        }
        context.send(eb.build());
    }

    private void unicode(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();
        if (context.getConcatArgs().codePoints().count() > 10) {
            context.send(CommandIcons.ERROR + context.getTranslated("emote.invalid"));
        } else {
            StringBuilder utf8 = new StringBuilder();
            StringBuilder unicode = new StringBuilder();
            context.getConcatArgs().codePoints().forEachOrdered(code -> {
                char[] chars = Character.toChars(code);
                StringBuilder hex = new StringBuilder(Integer.toHexString(code));
                while (hex.length() < 4)
                    hex.insert(0, "0");
                unicode.append("\\u").append(hex);

                if (chars.length > 1) {
                    String hex0 = StringUtil.zeroHexFill(Integer.toHexString(chars[0]));
                    String hex1 = StringUtil.zeroHexFill(Integer.toHexString(chars[1]));

                    utf8.append("\\u").append(hex0).append("\\u").append(hex1);
                }
            });
            eb.addField(context.getTranslated("emote.title"), String.format(context.getTranslated("emote.codepoint"), Character.getName(context.getConcatArgs().codePointAt(0)), unicode.toString(), utf8.toString()), false);
            context.send(eb.build());
        }
    }
}
