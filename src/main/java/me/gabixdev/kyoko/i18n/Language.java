package me.gabixdev.kyoko.i18n;

public enum Language {
    ENGLISH("en", "English", "\uD83C\uDDFA\uD83C\uDDF8"),
    ARABIC("ar", "\u0627\u0644\u0639\u064E\u0631\u064E\u0628\u0650\u064A\u064E\u0651\u0629", "\uD83C\uDDF8\uD83C\uDDE6"),
    GERMAN("de", "Deutsch", "\uD83C\uDDE9\uD83C\uDDEA"),
    POLISH("pl", "Polski", "\uD83C\uDDF5\uD83C\uDDF1");

    private String shortName;
    private String localized;
    private String emoji;

    private Language(String shortName, String localized, String emoji) {
        this.shortName = shortName;
        this.localized = localized;
        this.emoji = emoji;
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
}
