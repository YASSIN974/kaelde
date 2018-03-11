package me.gabixdev.kyoko.bot.util;

import me.gabixdev.kyoko.bot.Constants;
import me.gabixdev.kyoko.bot.command.CommandContext;
import net.dv8tion.jda.core.EmbedBuilder;

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

    public static void exception(CommandContext context) {
        EmbedBuilder err = context.getErrorEmbed();
        err.addField(context.getTranslated("generic.error"), String.format(context.getTranslated("generic.error.message"), Constants.DISCORD_URL), false);
        context.send(err.build());
        context.send(Constants.DISCORD_URL);
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
