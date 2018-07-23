package moe.kyokobot.bot.command;

import io.sentry.Sentry;
import lombok.Getter;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
    protected boolean experimental = false;
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
        if (!context.getGuild().getSelfMember().hasPermission(context.getChannel(), Permission.MESSAGE_WRITE))
            return;

        if (context.hasArgs()) {
            String subcommand = context.getArgs()[0].toLowerCase();
            if (subcommand.equalsIgnoreCase("-help") || subcommand.equalsIgnoreCase("--help")) {
                CommonErrors.usage(context);
                return;
            } else if (subCommands.containsKey(subcommand)) {
                Method m = subCommands.get(subcommand);
                try {
                    m.invoke(this, context);
                } catch (InsufficientPermissionException e) {
                    CommonErrors.noPermissionBot(context, e);
                } catch (Exception e) {
                    logger.error("Caught error while executing command \"" + name + "\"", e);
                    Sentry.capture(e);
                    CommonErrors.exception(context, e);
                }
                return;
            }
        }
        execute(context);
    }

    public void execute(@Nonnull CommandContext context) {
        throw new UnsupportedOperationException("Command has not implemented execute()");
    }
}
