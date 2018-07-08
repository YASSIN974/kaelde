package moe.kyokobot.bot.i18n;

import java.util.Locale;

public enum Language {
    DEFAULT(null, null, null, null),
    ENGLISH("en", "English", "\uD83C\uDDFA\uD83C\uDDF8", Locale.ENGLISH),
    NYNORSK("nn", "Norsk (Nynorsk)", "\uD83C\uDDF3\uD83C\uDDF4", new Locale("no_NO_NY")),
    POLISH("pl", "Polski", "\uD83C\uDDF5\uD83C\uDDF1", new Locale("pl_PL"));

    private String shortName;
    private String localized;
    private String emoji;
    private Locale locale;

    private Language(String shortName, String localized, String emoji, Locale locale) {
        this.shortName = shortName;
        this.localized = localized;
        this.emoji = emoji;
        this.locale = locale;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLocalized() {
        return localized;
    }

    public String getEmoji() {
        return emoji;
    }

    public Locale getLocale() {
        return locale;
    }
}
