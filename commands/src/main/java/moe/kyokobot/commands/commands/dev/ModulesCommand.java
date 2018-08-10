package moe.kyokobot.commands.commands.dev;

import com.google.common.base.Joiner;
import moe.kyokobot.bot.command.*;
import moe.kyokobot.bot.manager.ModuleManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
                    context.error("Module  `" + modname + "` is already started!");
                } else {
                    moduleManager.startModule(modname);
                    context.success("Module `" + modname + "` started!");
                }
            } else {
                context.error("Cannot find module `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void stop(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) printModules(context);
        else {
            if (modname.equalsIgnoreCase("core")) {
                context.error("Cannot stop `core` module!");
            } else if (moduleManager.isLoaded(modname)) {
                if (moduleManager.isStarted(modname)) {
                    moduleManager.stopModule(modname);
                    context.success("Module `" + modname + "` stopped!");
                } else {
                    context.error("Module  `" + modname + "` is already stopped!");
                }
            } else {
                context.error("Cannot find module `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void unload(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) printModules(context);
        else {
            if (modname.equalsIgnoreCase("core")) {
                context.error("Cannot unload `core` module!");
            } else if (moduleManager.isLoaded(modname)) {
                if (moduleManager.isStarted(modname)) {
                    moduleManager.stopModule(modname);
                }
                moduleManager.unload(modname, true);
                context.success("Module `" + modname + "` unloaded!");
            } else {
                context.error("Cannot find module `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void load(CommandContext context) {
        String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
        if (modname.isEmpty()) context.send(CommandIcons.ERROR + "Please specify module path!");
        else {
            File f = new File(modname);
            if (f.exists()) {
                try (ZipFile zipFile = new ZipFile(f)) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.getName().equals("plugin.json")) {
                            moduleManager.load(f.getAbsolutePath());
                            context.send(CommandIcons.SUCCESS + "Module loaded!");
                            return;
                        }
                    }
                    context.error("Not a valid module!");
                } catch (Exception e) {
                    logger.error("Error while loading module {}", modname, e);
                    context.error("Error while loading module `" + modname + "`: " + e.getMessage());
                }
            } else {
                context.error("Cannot find file `" + modname + "`");
            }
        }
    }

    @SubCommand
    public void reloadall(CommandContext context) {
        context.send(CommandIcons.WORKING + "Reloading all modules...", msg -> {
            try {
                moduleManager.loadModules();
                msg.editMessage(CommandIcons.SUCCESS + "Modules reloaded!").queue();
            } catch (Exception e) {
                logger.error("Error while reloading modules!", e);
                msg.editMessage(CommandIcons.ERROR + "Error reloading modules: " + e.getMessage()).queue();
            }
        });
    }

    @Override
    public void execute(@NotNull CommandContext context) {
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
