package moe.kyokobot.bot.command.debug;

import com.google.common.base.Joiner;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.manager.ModuleManager;

import java.util.Arrays;

public class ModulesCommand extends Command {
    private ModuleManager moduleManager;
    private Runtime rt = Runtime.getRuntime();

    public ModulesCommand(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;

        name = "modules";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 0) {
            printModules(context);
        } else {
            String modname = Joiner.on(" ").join(Arrays.stream(context.getArgs()).skip(1).toArray()).toLowerCase();
            switch (context.getArgs()[0].toLowerCase()) {
                case "start":
                    if (modname.isEmpty()) printModules(context);
                    else {
                        if (moduleManager.getStarted().contains(modname)) {
                            context.send(context.success() + "Module `" + modname + "` unloaded!");
                        } else {
                            context.send(context.error() + "Cannot find module `" + modname + "`");
                        }
                    }
                    break;
                case "stop":
                    if (modname.isEmpty()) printModules(context);
                    else {
                        if (moduleManager.getStarted().contains(modname)) {
                            context.send(context.success() + "Module `" + modname + "` unloaded!");
                        } else {
                            context.send(context.error() + "Cannot find module `" + modname + "`");
                        }
                    }
                    break;
                case "reload":
                    context.send(context.working() + "Reloading all modules...", msg -> {
                        try {
                            moduleManager.loadModules();
                            msg.editMessage(context.success() + "Modules reloaded!").queue();
                        } catch (Exception e) {
                            msg.editMessage(context.error() + "Error reloading modules: " + e.getMessage()).queue();
                            e.printStackTrace();
                        }
                    });
                    break;
                case "ram":
                    long free = rt.freeMemory() / 1024;
                    long total = rt.totalMemory() / 1024;
                    long used = total - free;
                    context.send("```\nFree: " + free + "KB\nTotal: " + total + "KB\nUsed: " + used + "KB\n```");
                    break;
                default:
                    printModules(context);
                    break;
            }
        }
    }

    private void printModules(CommandContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("```markdown\n");
        sb.append("# Modules loaded: ").append(moduleManager.getModules().size()).append("\n");
        sb.append(Joiner.on(", ").join(moduleManager.getModules().keySet())).append("\n");
        sb.append("# Modules started: ").append(moduleManager.getStarted().size()).append("\n");
        sb.append(Joiner.on(", ").join(moduleManager.getStarted())).append("\n");
        sb.append("```");
        context.send(sb.toString());
    }
}
