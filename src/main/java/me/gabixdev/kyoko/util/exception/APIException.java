package me.gabixdev.kyoko.util.exception;

public class APIException extends Exception {
    private final String message;
    private final String raw;

    public APIException(String message, String raw) {
        this.message = message;
        this.raw = raw;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return message;
    }

    public String getRaw() {
        return raw;
    }
}
