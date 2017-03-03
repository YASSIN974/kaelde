package com.programmingwizzard.charrizard.bot.command;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.programmingwizzard.charrizard.bot.Charrizard;
import com.programmingwizzard.charrizard.bot.command.basic.AbstractEmbedBuilder;
import com.programmingwizzard.charrizard.bot.response.ResponseException;
import com.programmingwizzard.charrizard.bot.response.kiciusie.KiciusieMode;
import com.programmingwizzard.charrizard.bot.response.kiciusie.KiciusieResponse;
import com.programmingwizzard.charrizard.bot.response.kiciusie.KiciusieResponses;
import com.programmingwizzard.charrizard.util.CharCodes;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class MiscCommands extends AbstractEmbedBuilder {

    private final Charrizard charrizard;
    private final ChatterBotFactory factory;
    private final KiciusieResponses kiciusieResponses = new KiciusieResponses();

    private ChatterBot bot;

    private final Cache<String, ChatterBotSession> sessionCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public MiscCommands(Charrizard charrizard) {
        this.charrizard = charrizard;
        this.factory = new ChatterBotFactory();
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

    @CommandInfo(name = "clever", description = "Talk with CleverBot!", usage = "<text>", min = 2)
    public void cleverbotCommand(Message message, CommandContext context) {
        if (bot == null) {
            try {
                bot = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception ex) {
                message.getChannel().sendMessage(getErrorBuilder().addField("Error", "The problem during query execution! See the console!", false).build()).queue();
                ex.printStackTrace();
                return;
            }
            ChatterBotSession session = sessionCache.getIfPresent(message.getAuthor().getId());
            if (session == null) {
                session = bot.createSession(Locale.ENGLISH);
                sessionCache.put(message.getAuthor().getId(), session);
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < context.getParamsLength(); i++) {
                builder.append(context.getParam(i)).append(" ");
            }
            try {
                String s = session.think(builder.toString());
                if (s == null) {
                    message.getChannel().sendMessage(getErrorBuilder().addField("Error", "The problem during query execution! See the console!", false).build()).queue();
                    return;
                }
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " - " + s);
            } catch (Exception ex) {
                message.getChannel().sendMessage(getErrorBuilder().addField("Error", "The problem during query execution! See the console!", false).build()).queue();
                ex.printStackTrace();
                return;
            }
        }
    }

    @CommandInfo(name = "cat", description = "Sends an kawaii neko!", usage = "<random|image|gif>", min = 2)
    public void kiciusieCommand(Message message, CommandContext context) {
        KiciusieMode mode;
        try {
            mode = KiciusieMode.valueOf(context.getParam(1).toUpperCase());
        } catch (IllegalArgumentException ex) {
            message.getChannel().sendMessage(getErrorBuilder().addField("Usage", "!cat <random|image|gif>", false).build()).queue();
            return;
        }
        try {
            KiciusieResponse response = kiciusieResponses.call(mode);
            message.getChannel().sendMessage(getNormalBuilder().addField("Cat", "powered by kiciusie.pl", true).setImage(response.getImageUrl()).build()).queue();
        } catch (ResponseException ex) {
            message.getChannel().sendMessage(getErrorBuilder().addField("Error", "The problem during query execution! See the console!", false).build()).queue();
            ex.printStackTrace();
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
