package moe.kyokobot.bot.command.debug;

import com.google.common.base.Joiner;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.command.SubCommand;
import moe.kyokobot.bot.manager.ModuleManager;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static moe.kyokobot.bot.command.CommandContext.*;

public class ModulesCommand extends Command {
    private ModuleManager moduleManager;
    private Runtime rt = Runtime.getRuntime();

    public ModulesCommand(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;

        name = "modules";
        type = CommandType.DEBUG;
    }

    @SubCommand
    public void start(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) printModules(context);
        else {
            if (moduleManager.isLoaded(modname)) {
                if (moduleManager.isStarted(modname)) {
                    context.send(error() + "Module  `" + modname + "` is already started!");
                } else {
                    moduleManager.startModule(modname);
                    context.send(success() + "Module `" + modname + "` started!");
                }
            } else {
                context.send(error() + "Cannot find module `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void stop(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) printModules(context);
        else {
            if (modname.equalsIgnoreCase("core")) {
                context.send(error() + "Cannot stop `core` module!");
            } else if (moduleManager.isLoaded(modname)) {
                if (moduleManager.isStarted(modname)) {
                    moduleManager.stopModule(modname);
                    context.send(success() + "Module `" + modname + "` stopped!");
                } else {
                    context.send(error() + "Module  `" + modname + "` is already stopped!");
                }
            } else {
                context.send(error() + "Cannot find module `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void unload(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) printModules(context);
        else {
            if (modname.equalsIgnoreCase("core")) {
                context.send(error() + "Cannot unload `core` module!");
            } else if (moduleManager.isLoaded(modname)) {
                if (moduleManager.isStarted(modname)) {
                    moduleManager.stopModule(modname);
                }
                moduleManager.unload(modname, true);
                context.send(success() + "Module `" + modname + "` unloaded!");
            } else {
                context.send(error() + "Cannot find module `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void load(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) context.send(error() + "Please specify module path!");
        else {
            File f = new File(modname);
            if (f.exists()) {
                try {
                    ZipFile zipFile = new ZipFile(f);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.getName().equals("plugin.json")) {
                            moduleManager.load(f.getAbsolutePath());
                            context.send(success() + "Module loaded!");
                            return;
                        }
                    }
                    context.send(error() + "Not a valid module!");
                } catch (Exception e) {
                    e.printStackTrace();
                    context.send(error() + "Error while loading module `" + modname + "`: " + e.getMessage());
                }
            } else {
                context.send(error() + "Cannot find file `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void reloadall(CommandContext context) {
        context.send(working() + "Reloading all modules...", msg -> {
            try {
                moduleManager.loadModules();
                msg.editMessage(success() + "Modules reloaded!").queue();
            } catch (Exception e) {
                msg.editMessage(error() + "Error reloading modules: " + e.getMessage()).queue();
                e.printStackTrace();
            }
        });
    }

    @SubCommand
    public void ram(CommandContext context) {
        long free = rt.freeMemory() / 1024;
        long total = rt.totalMemory() / 1024;
        long used = total - free;
        context.send("```css\nFree: " + free + "KB\nTotal: " + total + "KB\nUsed: " + used + "KB\n```");
    }

    @Override
    public void execute(CommandContext context) {
        printModules(context);
    }

    private void printModules(CommandContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("```css\n");
        sb.append("Modules loaded (").append(moduleManager.getModules().size()).append("): ");
        sb.append(Joiner.on(", ").join(moduleManager.getModules().keySet())).append("\n");
        sb.append("Modules started (").append(moduleManager.getStarted().size()).append("): ");
        sb.append(Joiner.on(", ").join(moduleManager.getStarted())).append("\n");
        sb.append("```");
        context.send(sb.toString());
    }
}
