package me.gabixdev.kyoko.i18n;

public enum Language {
    ENGLISH("en");

    private String shortName;
    private Language(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
