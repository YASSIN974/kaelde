package me.gabixdev.kyoko.shared.util;

import org.fusesource.jansi.Ansi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ColoredFormatter extends Formatter {
    private static final DateFormat df = new SimpleDateFormat("hh:mm:ss");

    private static final String ERROR_COLOR = Ansi.ansi().bold().fg(Ansi.Color.RED).toString();
    private static final String WARNING_COLOR = Ansi.ansi().bold().fg(Ansi.Color.YELLOW).toString();
    private static final String INFO_COLOR = Ansi.ansi().bold().fg(Ansi.Color.GREEN).toString();
    private static final String DEBUG_COLOR = Ansi.ansi().bold().fg(Ansi.Color.CYAN).toString();
    private static final String RESET = Ansi.ansi().reset().toString();

    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        if (Ansi.isEnabled()) builder.append(getColor(record.getLevel()));
        builder.append("[").append(df.format(new Date(record.getMillis()))).append("] ");
        builder.append("[").append(record.getLevel()).append("] ");
        builder.append(record.getMessage());
        if (Ansi.isEnabled()) builder.append(RESET);
        builder.append("\n");
        return builder.toString();
    }

    private String getColor(Level l) {
        if (l == Level.SEVERE) // why that's not enum ;/
            return ERROR_COLOR;
        else if (l == Level.WARNING)
            return WARNING_COLOR;
        else if (l == Level.INFO)
            return INFO_COLOR;
        else
            return DEBUG_COLOR;
    }
}
