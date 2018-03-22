package me.gabixdev.kyoko.bot.command.normal.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.CommonErrors;
import me.gabixdev.kyoko.bot.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoteCommand extends Command {
    private static final Pattern emojiPatten = Pattern.compile("<:.*:(\\d+)>");
    private final Kyoko kyoko;

    public EmoteCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "emote";
        this.aliases = new String[]{"emoji", "emoteinfo"};
        this.category = CommandCategory.UTILITY;
        this.description = "emote.description";
        this.usage = "emote.usage";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            CommonErrors.usage(context);
        } else {
            EmbedBuilder eb = context.getNormalEmbed();
            final Matcher matcher = emojiPatten.matcher(context.getConcatArgs());

            if (matcher.matches()) {
                String id = matcher.replaceFirst("$1");
                String url = "https://cdn.discordapp.com/emojis/" + id + ".png";
                eb.setThumbnail(url);
                Emote emote = kyoko.getJda().getEmoteById(id);
                if (emote == null) {
                    eb.addField(context.getTranslated("emote.title"), String.format(context.getTranslated("emote.custom"), "???", id, "???", url), false);
                } else {
                    eb.addField(context.getTranslated("emote.title"), String.format(context.getTranslated("emote.custom"), emote.getName(), id, emote.getGuild(), url), false);
                }
            } else {
                if (context.getConcatArgs().codePoints().count() > 10) {
                    eb = context.getErrorEmbed();
                    eb.addField(context.getTranslated("generic.error"), context.getTranslated("emote.invalid"), false);
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
                }
            }
            context.send(eb.build());
        }
    }
}
