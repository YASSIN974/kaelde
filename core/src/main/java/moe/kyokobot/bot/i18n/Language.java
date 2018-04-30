package moe.kyokobot.bot.i18n;

public enum Language {
    ENGLISH("en", "English", "\uD83C\uDDFA\uD83C\uDDF8");

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
