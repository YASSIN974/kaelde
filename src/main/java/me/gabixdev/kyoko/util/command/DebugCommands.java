package me.gabixdev.kyoko.util.command;

import me.gabixdev.kyoko.BlinkThread;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.util.GsonUtil;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;

public class DebugCommands {
    public static void handle(Kyoko kyoko, MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().startsWith("help")) {
            e.getChannel().sendMessage("```markdown\n" +
                    "# Kyoko debug console\n\n" +
                    "help - display help\n" +
                    "gwtest - set gateway guild count to random\n" +
                    "reload - reload config\n" +
                    "setname - set bot name\n" +
                    "eval - eval js code\n" +
                    "prune - emergency server cleanup" +
                    "```"
            ).queue();
        } else if (e.getMessage().getContentRaw().startsWith("gwtest")) {
            if (kyoko.getSettings().isGateway()) {
                int gc = (int) Math.floor(Math.random() * 1000);
                kyoko.getJda().getPresence().setGame(Game.of(Game.GameType.DEFAULT, "kgw:gc:" + gc));
            } else {
                e.getChannel().sendMessage("gateway is not enabled!").queue();
            }
        } else if (e.getMessage().getContentRaw().startsWith("reload")) {
            try {
                Settings settings = GsonUtil.readConfiguration(Settings.class, new File("config.json"));
                kyoko.setSettings(settings);
                kyoko.initJS();
                kyoko.getBlinkThread().interrupt();

                if (!settings.isGateway()) {
                    if (settings.getGame() != null && !settings.getGame().isEmpty()) {
                        kyoko.setBlinkThread(new Thread(new BlinkThread(kyoko)));
                        kyoko.getBlinkThread().start();
                    } else {
                        kyoko.getJda().getPresence().setGame(null);
                    }
                } else {
                    if (kyoko.getBlinkThread() != null) {
                        kyoko.getBlinkThread().interrupt();
                        kyoko.setBlinkThread(null);
                    }
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
        } else if (e.getMessage().getContentRaw().startsWith("prune ")) {
            String[] args = e.getMessage().getContentRaw().split(" ");
            if (args.length != 3) {
                e.getChannel().sendMessage("prune <guild id> <author id>").queue();
                return;
            }

            long guild = Long.parseLong(args[1]);
            long author = Long.parseLong(args[2]);

            for (TextChannel t : kyoko.getJda().getGuildById(guild).getTextChannels()) {
                kyoko.getLog().info("Cleaning up: " + t.getName());
                try {
                    e.getChannel().sendMessage("Cleaning up: #" + t.getName()).queue();
                    t.getHistory().retrievePast(100).queue(suc -> {
                        suc.forEach(message -> {
                            if (message.getAuthor().getIdLong() == author) {
                                message.delete().queue();
                            }
                        });
                    });
                    e.getChannel().sendMessage("done!").queue();
                } catch (Exception ex) {
                    e.getChannel().sendMessage("Clean up error, no permission?").queue();
                    ex.printStackTrace();
                }
            }
        } else if (kyoko.getSettings().isEvalEnabled() && e.getMessage().getContentRaw().startsWith("eval ")) {
            String code = e.getMessage().getContentRaw().substring(5);
            try {
                Object out = kyoko.getScriptEngine().eval(code);
                if (out == null) {
                    e.getChannel().sendMessage("Null output").queue();
                } else {
                    String dat = out.toString();
                    dat = dat.replace(kyoko.getSettings().getToken(), "[censored]")
                            .replace(kyoko.getSettings().getMysqlDatabase(), "[censored]")
                            .replace(kyoko.getSettings().getMysqlHost(), "[censored]")
                            .replace(kyoko.getSettings().getMysqlUser(), "[censored]")
                            .replace(kyoko.getSettings().getMysqlPassword(), "[censored]")
                            .replace(kyoko.getSettings().getNicoMail(), "[censored]")
                            .replace(kyoko.getSettings().getNicoPassword(), "[censored]")
                            .replace(kyoko.getSettings().getYoutubeApiKey(), "[censored]");
                    e.getChannel().sendMessage("```\n" + dat + "\n```").queue();
                }
            } catch (Exception ex) {
                e.getChannel().sendMessage("**Error:** " + ex.getMessage()).queue();
                ex.printStackTrace();
            }
        }
    }
}
