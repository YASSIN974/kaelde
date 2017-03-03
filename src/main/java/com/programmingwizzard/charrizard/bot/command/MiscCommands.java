package com.programmingwizzard.charrizard.bot.command;

import com.programmingwizzard.charrizard.bot.Charrizard;
import com.programmingwizzard.charrizard.bot.command.basic.AbstractEmbedBuilder;
import com.programmingwizzard.charrizard.util.CharCodes;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;

import java.util.LinkedHashSet;
import java.util.Set;

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
                for (int i = 2; i < context.getParamsLength(); i++) {
                    for (char c : context.getParam(i).toLowerCase().toCharArray()) {
                        builder.append(toRegionalIndicator(c)).append(" ");
                    }
                    builder.append("   ");
                }
                channel.sendMessage(builder.toString()).queue();
                break;
            case "raw":
                builder.append("```");
                for (int i = 2; i < context.getParamsLength(); i++) {
                    for (char c : context.getParam(i).toLowerCase().toCharArray()) {
                        builder.append(toRegionalIndicator(c)).append(" ");
                    }
                    builder.append("   ");
                }
                builder.append("```");
                channel.sendMessage(builder.toString()).queue();
                break;
            case "react":
                Set<String> reactions = new LinkedHashSet<>();
                for (int i = 2; i < context.getParamsLength(); i++) {
                    for (char c : context.getParam(i).toLowerCase().toCharArray()) {
                        String reaction = toRegionalIndicator(c);
                        if (!reaction.isEmpty()) {
                            reactions.add(reaction);
                        }
                    }
                }
                for (String reaction : reactions) {
                    message.addReaction(reaction).queue();
                }
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
