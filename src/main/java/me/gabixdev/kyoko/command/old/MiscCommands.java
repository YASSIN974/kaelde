package me.gabixdev.kyoko.command.old;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.remoteapi.ResponseException;
import me.gabixdev.kyoko.remoteapi.kiciusie.KiciusieMode;
import me.gabixdev.kyoko.remoteapi.kiciusie.KiciusieResponse;
import me.gabixdev.kyoko.remoteapi.kiciusie.KiciusieResponses;
import me.gabixdev.kyoko.util.CharCodes;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class MiscCommands {

    private final Kyoko kyoko;
    //private final ChatterBotFactory factory;
    //private final KiciusieResponses kiciusieResponses = new KiciusieResponses();

    //private ChatterBot bot;

    //private final Cache<String, ChatterBotSession> sessionCache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();

    public MiscCommands(Kyoko kyoko) {
        this.kyoko = kyoko;
        //this.factory = new ChatterBotFactory();
    }

    /*
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
                channel.sendMessage(kyoko.getAbstractEmbedBuilder().getUsageBuilder("bigtext", "<print|raw|react> <text>").build());
                break;
        }
    }

    public void cleverbotCommand(Message message, CommandContext context) {
        MessageEmbed error = kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField("Error", "The problem during query execution! Please report to bot authors!", false).build();
        if (bot == null) {
            try {
                bot = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception ex) {
                message.getChannel().sendMessage(error).queue();
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
                    message.getChannel().sendMessage(error).queue();
                    return;
                }
                message.getChannel().sendMessage(new StringBuilder(message.getAuthor().getAsMention()).append(" - ").append(s).toString());
            } catch (Exception ex) {
                message.getChannel().sendMessage(error).queue();
                ex.printStackTrace();
                return;
            }
        }
    }

    public void kiciusieCommand(Message message, CommandContext context) {
        KiciusieMode mode;
        try {
            mode = KiciusieMode.valueOf(context.getParam(1).toUpperCase());
        } catch (IllegalArgumentException ex) {
            message.getChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField("Usage", "!cat <random|image|gif>", false).build()).queue();
            return;
        }
        try {
            KiciusieResponse response = kiciusieResponses.call(mode);
            message.getChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getNormalBuilder().addField("Cat", "powered by kiciusie.pl", true).setImage(response.getImageUrl()).build()).queue();
        } catch (ResponseException ex) {
            message.getChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField("Error", "The problem during query execution! See the console!", false).build()).queue();
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
    */
}
