package me.gabixdev.kyoko.util.command;

public class CommandUsageException extends CommandException {
    public CommandUsageException() {
        super();
    }

    public CommandUsageException(String message) {
        super(message);
    }
}
