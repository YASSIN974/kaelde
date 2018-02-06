package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.BlinkThread;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.util.GsonUtil;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;

public class DebugCommands {
    public static void handle(Kyoko kyoko, MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().startsWith("help")) {
            e.getChannel().sendMessage("```markdown\n" +
                    "# Kyoko debug console\n\n" +
                    "help - display help\n" +
                    "reload - reload config\n" +
                    "setname - set bot name\n" +
                    "```"
            ).queue();
        } else if (e.getMessage().getContentRaw().startsWith("reload")) {
            try {
                Settings settings = GsonUtil.readConfiguration(Settings.class, new File("config.json"));
                kyoko.setSettings(settings);
                kyoko.getBlinkThread().interrupt();

                if (settings.getGame() != null && !settings.getGame().isEmpty()) {
                    kyoko.setBlinkThread(new Thread(new BlinkThread(kyoko)));
                    kyoko.getBlinkThread().start();
                } else {
                    kyoko.getJda().getPresence().setGame(null);
                }

                e.getChannel().sendMessage("configuration reloaded!").queue();
            } catch (Exception ex) {
                ex.printStackTrace();
                e.getChannel().sendMessage("reload failed").queue();
            }
        } else if (e.getMessage().getContentRaw().startsWith("setname ")) {
            String name = e.getMessage().getContentRaw().substring(8);
            kyoko.getJda().getSelfUser().getManager().setName(name).queue();
            e.getChannel().sendMessage("Bot name set to: `" + name + "`").queue();
        }
    }
}
