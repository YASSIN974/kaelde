package moe.kyokobot.bot.i18n;

public enum Language {
    ENGLISH("en", "English", "\uD83C\uDDFA\uD83C\uDDF8"),
    ARABIC("ar", "\u0627\u0644\u0639\u064E\u0631\u064E\u0628\u0650\u064A\u064E\u0651\u0629", "\uD83C\uDDF8\uD83C\uDDE6"),
    GERMAN("de", "Deutsch", "\uD83C\uDDE9\uD83C\uDDEA"),
    POLISH("pl", "Polski", "\uD83C\uDDF5\uD83C\uDDF1"),
    BULGARIAN("bg", "български", "\uD83C\uDDE7\uD83C\uDDEC"),
    FRENCH("fr", "Français", "\uD83C\uDDEB\uD83C\uDDF7"),
    JAPANESE("ja", "\u65E5\u672C\u8A9E", "\uD83C\uDDEF\uD83C\uDDF5");

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