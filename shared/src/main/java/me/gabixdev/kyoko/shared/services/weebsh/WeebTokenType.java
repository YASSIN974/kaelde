package me.gabixdev.kyoko.shared.services.weebsh;

public enum WeebTokenType {
    WOLKE("Wolke "),
    BEARER("Bearer ");

    private final String prefix;

    private WeebTokenType(String prefix) {
        this.prefix = prefix;
    }
}
