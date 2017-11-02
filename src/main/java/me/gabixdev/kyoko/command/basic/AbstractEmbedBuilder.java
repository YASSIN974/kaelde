package me.gabixdev.kyoko.command.basic;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

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
                .setUrl(Constants.GITHUB_URL)
                .setColor(kyoko.getSettings().getNormalColor());
    }

    public final EmbedBuilder getErrorBuilder() {
        return new EmbedBuilder()
                .setFooter(footer, null)
                .setUrl(Constants.GITHUB_URL)
                .setColor(kyoko.getSettings().getErrorColor());
    }

    public final EmbedBuilder getUsageBuilder(String commandName, String usage) {
        return new EmbedBuilder()
                .setFooter(footer, null)
                .setUrl(Constants.GITHUB_URL)
                .setColor(kyoko.getSettings().getErrorColor());
    }
}
