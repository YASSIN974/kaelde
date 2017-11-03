package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.EmbedBuilder;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class AbstractEmbedBuilder {
    private final Kyoko kyoko;

    public AbstractEmbedBuilder(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    private static final String footer = "Kyoko v" + Constants.VERSION + " | created by gabixdev & contributors";

    public EmbedBuilder getNormalBuilder() {
        return new EmbedBuilder()
                .setFooter(footer, null)
                //.setUrl(Constants.GITHUB_URL)
                .setColor(kyoko.getSettings().getNormalColor());
    }

    public final EmbedBuilder getSuccessBuilder() {
        return new EmbedBuilder()
                .setFooter(footer, null)
                //.setUrl(Constants.GITHUB_URL)
                .setColor(kyoko.getSettings().getSuccessColor());
    }

    public final EmbedBuilder getErrorBuilder() {
        return new EmbedBuilder()
                .setFooter(footer, null)
                //.setUrl(Constants.GITHUB_URL)
                .setColor(kyoko.getSettings().getErrorColor());
    }

    public final EmbedBuilder getUsageBuilder(Language lang, String commandName, String usage) {
        return new EmbedBuilder()
                .setFooter(footer, null)
                .addField(kyoko.getI18n().get(lang, "generic.usage"), new StringBuilder(kyoko.getSettings().getPrefix()).append(commandName).append(" ").append(usage).toString(), true)
                .setColor(kyoko.getSettings().getErrorColor());
    }
}
