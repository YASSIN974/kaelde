package me.gabixdev.kyoko.bot.command.debug;

import me.gabixdev.kyoko.bot.Constants;
import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.command.CommandType;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellCommand extends Command {
    private final Kyoko kyoko;

    public ShellCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "shell";
        this.category = CommandCategory.UTILITY;
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

            data = data.replace(kyoko.getSettings().token, "[censored]");
            context.send("```\n" + data + "```");
        } catch (Exception e) {
            e.printStackTrace();
            context.send(Constants.ERROR_MARK + "Error while running shell command: " + e.getMessage());
        }
    }
}
