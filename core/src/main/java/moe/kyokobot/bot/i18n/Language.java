package moe.kyokobot.bot.i18n;

import java.util.Locale;

public enum Language {
    DEFAULT(null, null, null, null),
    BULGARIAN("bg", "български", "\uD83C\uDDE7\uD83C\uDDEC", new Locale("bg_BG")),
    FRENCH("fr", "Français", "\uD83C\uDDE8\uD83C\uDDF5", new Locale("fr_FR")),
    GERMAN("de", "Deutsch", "\uD83C\uDDE9\uD83C\uDDEA", new Locale("de_DE")),
    ENGLISH("en", "English", "\uD83C\uDDFA\uD83C\uDDF8", new Locale("en_US")),
    NORWEGIAN_BO("nb", "Norsk (Bokmål)", "\uD83C\uDDF3\uD83C\uDDF4", new Locale("no_NO")),
    NORWEGIAN_NY("nn", "Norsk (Nynorsk)", "\uD83C\uDDF3\uD83C\uDDF4", new Locale("no_NO_NY")),
    POLISH("pl", "Polski", "\uD83C\uDDF5\uD83C\uDDF1", new Locale("pl_PL"));

    private String shortName;
    private String localized;
    private String emoji;
    private Locale locale;

    Language(String shortName, String localized, String emoji, Locale locale) {
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
