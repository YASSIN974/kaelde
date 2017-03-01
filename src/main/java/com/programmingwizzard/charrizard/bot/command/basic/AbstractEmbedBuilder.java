package com.programmingwizzard.charrizard.bot.command.basic;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class AbstractEmbedBuilder {

    protected final EmbedBuilder getNormalBuilder() {
        return new EmbedBuilder()
                       .setTitle("Charrizard")
                       .setFooter("© 2017 Charrizard contributors", null)
                       .setUrl("https://github.com/CharrizardBot/Charrizard/")
                       .setColor(new Color(46, 204, 113));
    }

}
