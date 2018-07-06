package moe.kyokobot.bot.util;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.TimeUnit;

public class CommonErrors {
    public static void noPermissionUser(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), context.getTranslated("generic.usernoperm"), false);
        context.send(err.build());
    }

    public static void noPermissionBot(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), context.getTranslated("generic.botnoperm"), false);
        context.send(err.build());
    }

    public static void noUserFound(CommandContext context, String user) {
        context.send(CommandIcons.ERROR + String.format(context.getTranslated("generic.usernotfound"), user));
    }

    public static void editNoUserFound(CommandContext context, String user, Message message) {
        message.editMessage(CommandIcons.ERROR + String.format(context.getTranslated("generic.usernotfound"), user)).override(true).queue();
    }

    public static void editException(CommandContext context, Throwable e, Message message) {
        message.editMessage(CommandIcons.ERROR + String.format(context.getTranslated("generic.error.message"), Constants.DISCORD_URL, Constants.DEBUG ? "\n\n`" + e.getClass().getCanonicalName() + ": " + e.getMessage() + "`" : "")).override(true).queue();
    }

    public static void exception(CommandContext context, Throwable e) {
        context.send(CommandIcons.ERROR + String.format(context.getTranslated("generic.error.message"), Constants.DISCORD_URL, Constants.DEBUG ? "\n\n`" + e.getClass().getCanonicalName() + ": " + e.getMessage() + "`" : ""));
    }

    public static void notANumber(CommandContext context, String arg) {
        context.send(CommandIcons.ERROR + context.getTranslated("generic.notanumber"));
    }

    public static void cooldown(CommandContext context) {
        context.send(CommandIcons.ERROR + context.getTranslated("generic.cooldown"), message -> message.delete().queueAfter(2, TimeUnit.SECONDS));
    }

    public static void owner(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), context.getTranslated("generic.guildowner"), false);
        context.send(err.build());
    }

    public static void usage(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.usage"), "`" + context.getPrefix() + context.getCommand().getName() + (context.getCommand().getUsage() != null ? (" " + context.getTranslated(context.getCommand().getUsage())) : "") + "`", false);
        context.send(err.build());
    }
}