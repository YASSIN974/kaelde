package moe.kyokobot.bot.util;

import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.CommandContext;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

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
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), String.format(context.getTranslated("generic.usernotfound"), user), false);
        context.send(err.build());
    }

    public static void devOnly(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), context.getTranslated("generic.execlimit"), false);
        context.send(err.build());
    }

    public static void editException(CommandContext context, Throwable e, Message message) {
        EmbedBuilder err = context.getErrorEmbed();
        MessageBuilder mb = new MessageBuilder();
        mb.append(" ");
        err.addField(context.getTranslated("generic.error"), String.format(context.getTranslated("generic.error.message"), Constants.DISCORD_URL, Constants.DEBUG ? "\n\n`" + e.getClass().getCanonicalName() + ": " + e.getMessage() + "`" : ""), false);
        mb.setEmbed(err.build());
        message.editMessage(mb.build()).queue();
    }

    public static void exception(CommandContext context, Throwable e) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), String.format(context.getTranslated("generic.error.message"), Constants.DISCORD_URL, Constants.DEBUG ? "\n\n`" + e.getClass().getCanonicalName() + ": " + e.getMessage() + "`" : ""), false);
        context.send(err.build());
    }

    public static void notANumber(CommandContext context, String arg) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), String.format(context.getTranslated("generic.notanumber"), arg), false);
        context.send(err.build());
    }

    public static void noBanFound(CommandContext context, String arg) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), String.format(context.getTranslated("generic.bannotfound"), arg), false);
        context.send(err.build());
    }

    public static void cooldown(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), context.getTranslated("generic.cooldown"), false);
        context.send(err.build());
    }

    public static void owner(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), context.getTranslated("generic.guildowner"), false);
        context.send(err.build());
    }

    public static void usage(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.usage"), "`" + context.getPrefix() + context.getCommand().getName() + " " + context.getTranslated(context.getCommand().getUsage()) + "`", false);
        context.send(err.build());
    }
}