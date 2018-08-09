package moe.kyokobot.commands.commands.dev;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.util.CommonErrors;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;

public class ReloadSettingsCommand extends Command {
    public ReloadSettingsCommand() {
        name = "reloadsettings";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        String cfgname = System.getenv("KYOKO_CONFIG");
        if (cfgname == null) cfgname = System.getProperty("kyoko.config");

        File cfg = new File(cfgname != null ? cfgname : "config.json");
        Settings settings = null;
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        if (!cfg.exists()) {
            context.send(CommandIcons.ERROR + "Cannot find configuration file!");
        }

        try {
            settings = gson.fromJson(new FileReader(cfg), Settings.class);
        } catch (Exception e) {
            context.send(CommandIcons.ERROR + "Cannot read configuration file!");
            CommonErrors.exception(context, e);
        }

        if (settings != null) {
            Settings.instance = settings;
            context.send(CommandIcons.SUCCESS + "Settings reloaded!");
        }
    }
}
