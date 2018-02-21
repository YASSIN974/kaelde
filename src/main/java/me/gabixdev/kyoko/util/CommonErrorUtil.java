package me.gabixdev.kyoko.util;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommonErrorUtil {
    public static void noPermissionUser(Kyoko kyoko, Language l, TextChannel chan) {
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "generic.usernoperm"), false);
        chan.sendMessage(err.build()).queue();
    }

    public static void noPermissionBot(Kyoko kyoko, Language l, TextChannel chan) {
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "generic.botnoperm"), false);
        chan.sendMessage(err.build()).queue();
    }

    public static void noUserFound(Kyoko kyoko, Language l, TextChannel chan, String user) {
        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
        err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "generic.usernotfound"), user), false);
        chan.sendMessage(err.build()).queue();
    }

    public static void devOnly(Kyoko kyoko, Language l, TextChannel chan) {
        chan.sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "generic.execlimit"), false).build()).queue();
    }

    public static void exception(Kyoko kyoko, Language l, TextChannel chan) {
        chan.sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "generic.error.message"), Constants.DISCORD_URL), false).build()).queue();
        chan.sendMessage(Constants.DISCORD_URL).queue();
    }
}
