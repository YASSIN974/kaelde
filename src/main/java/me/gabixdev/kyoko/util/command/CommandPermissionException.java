package me.gabixdev.kyoko.util.command;

public class CommandPermissionException extends CommandException {
    public CommandPermissionException() {
        super();
    }

    public CommandPermissionException(String permission) {
        super(permission);
    }

    public String getPermission() {
        return this.getMessage();
    }
}
