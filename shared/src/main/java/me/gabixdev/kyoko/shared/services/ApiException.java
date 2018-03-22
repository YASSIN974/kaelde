package me.gabixdev.kyoko.shared.services;

public class ApiException extends Exception {
    public ApiException() {
        super("Invalid token");
    }

    public ApiException(Throwable ex) {
        super("Invalid token", ex);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable ex) {
        super(message, ex);
    }
}
