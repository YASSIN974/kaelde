package me.gabixdev.kyoko.util.exception;

public class NotFoundException extends Exception {
    private final String query;

    public NotFoundException(String query) {
        super("Nothing found by \"" + query + "\"");
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
