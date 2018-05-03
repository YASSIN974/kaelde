package moe.kyokobot.bot.command.debug;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellCommand extends Command {
    public ShellCommand() {
        this.name = "shell";
        this.type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(context.getArgs());

            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            StringBuilder dat = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
                dat.append(line).append("\n");

            String data = dat.toString();
            if (data.length() > 1993) {
                data = data.substring(0, 1993);
            }

            context.send("```\n" + data + "```");
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
            context.send(context.error() + "Error while running shell command: " + e.getMessage());
        }
    }
}
