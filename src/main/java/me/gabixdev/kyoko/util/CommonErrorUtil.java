package me.gabixdev.kyoko.util;

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
}
