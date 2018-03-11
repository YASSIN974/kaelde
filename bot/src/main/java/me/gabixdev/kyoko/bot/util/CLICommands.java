package me.gabixdev.kyoko.bot.util;

import me.gabixdev.kyoko.bot.Kyoko;

import java.io.BufferedReader;

public class CLICommands {
    public static void runHandler(BufferedReader br, Kyoko kyoko) {
        while (kyoko.isRunning()) {
            try {
                String line = br.readLine();
                String[] args = line.split(" ");
                if (args.length != 0) {
                    switch (args[0].toLowerCase()) {
                        case "help":
                            kyoko.getLogger().info("help - show this help");
                            kyoko.getLogger().info("commands - list commands");
                            kyoko.getLogger().info("threads - list threads");
                            kyoko.getLogger().info("reload - reload frontend classloader");
                            break;
                        case "commands":
                            kyoko.getLogger().info("Command list: ");
                            kyoko.getCommandManager().getCommands().keySet().forEach(kyoko.getLogger()::info);
                            break;
                        case "threads":
                            kyoko.getLogger().info("Threads: ");
                            Thread.getAllStackTraces().keySet().forEach(thread -> kyoko.getLogger().info(thread.getId() + ": " + thread.getName() + " (" + thread.getPriority() + ")"));
                            break;
                        case "reload":
                            kyoko.getLogger().info("Reloading bot...");
                            kyoko.setRunning(false);
                            break;
                        case "exit":
                            kyoko.getLogger().info("Bye!");
                            kyoko.getJda().shutdown();
                            System.exit(0);
                            break;
                        default:
                            kyoko.getLogger().info("Unknown command, type \"help\" for help.");
                            break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
