package me.gabixdev.kyoko.shared;

import me.gabixdev.kyoko.shared.util.ColoredFormatter;
import org.fusesource.jansi.AnsiConsole;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class KyokoLogger {
    private Logger log;

    public KyokoLogger() {
        AnsiConsole.systemInstall();
        log = Logger.getLogger("Kyoko");
        log.setUseParentHandlers(false);
        ColoredFormatter formatter = new ColoredFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        log.addHandler(handler);
    }

    public Logger getLog() {
        return log;
    }
}
