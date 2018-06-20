package moe.kyokobot.bot.command;

import io.sentry.Sentry;
import lombok.Getter;
import moe.kyokobot.bot.util.CommonErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class Command {
    protected static Logger logger = LoggerFactory.getLogger(Command.class);
    @Getter
    protected String name;
    @Getter
    protected String[] aliases = new String[0];
    protected String usage;
    protected String description;
    @Getter
    protected CommandCategory category = null;
    @Getter
    protected CommandType type = CommandType.NORMAL;
    @Getter
    protected boolean allowedInDMs = false;
    @Getter
    protected HashMap<String, Method> subCommands = new HashMap<>();

    public String getUsage() {
        return usage == null ? name + ".usage" : usage;
    }

    public String getDescription() {
        return description == null ? name + ".description" : description;
    }

    public void onRegister() {
        //
    }

    public void onUnregister() {
        //
    }

    public void preExecute(CommandContext context) {
        if (context.hasArgs()) {
            String subcommand = context.getArgs()[0].toLowerCase();
            if (subcommand.equalsIgnoreCase("-help") || subcommand.equalsIgnoreCase("--help")) {
                CommonErrors.usage(context);
                return;
            } else if (subCommands.containsKey(subcommand)) {
                Method m = subCommands.get(subcommand);
                try {
                    m.invoke(this, context);
                } catch (Exception e) {
                    e.printStackTrace();
                    Sentry.capture(e);
                    CommonErrors.exception(context, e);
                }
                return;
            }
        }
        execute(context);
    }

    public void execute(CommandContext context) {
        throw new UnsupportedOperationException("Command has not implemented execute()");
    }
}
