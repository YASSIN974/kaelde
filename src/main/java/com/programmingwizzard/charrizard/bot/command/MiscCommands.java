package com.programmingwizzard.charrizard.bot.command;

import com.programmingwizzard.charrizard.bot.Charrizard;
import com.programmingwizzard.charrizard.bot.command.basic.AbstractEmbedBuilder;
import com.programmingwizzard.charrizard.util.CharCodes;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class MiscCommands extends AbstractEmbedBuilder {

    private final Charrizard charrizard;

    public MiscCommands(Charrizard charrizard) {
        this.charrizard = charrizard;
    }

    @CommandInfo(name = "bigtext", description = "Sends message from regional indicator characters!", usage = "<print|raw|react> <text>", min = 3)
    public void bigtextCommand(Message message, CommandContext context) {
        TextChannel channel = message.getTextChannel();
        StringBuilder builder = new StringBuilder();
        String action = context.getParam(1);
        switch (action.toLowerCase()) {
            case "print":
                break;
            case "raw":
                break;
            case "react":
                break;
            default:
                channel.sendMessage(getErrorBuilder().addField("Usage", "!bigtext <print|raw|react> <text>", false).build());
                break;
        }
    }

    private String toRegionalIndicator(char c) {
        if (c >= CharCodes.SMALL_A && c <= CharCodes.SMALL_Z) {
            c -= CharCodes.SMALL_A;
            return String.valueOf(Character.toChars(CharCodes.REGIONAL_INDICATOR_A + c));
        } else {
            return "";
        }
    }

}
