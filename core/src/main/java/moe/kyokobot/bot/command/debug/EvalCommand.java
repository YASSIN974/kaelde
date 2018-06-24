package moe.kyokobot.bot.command.debug;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalCommand extends Command {
    private final ModuleManager moduleManager;
    private final CommandManager commandManager;
    private final DatabaseManager databaseManager;

    private ScriptEngine engine;

    public EvalCommand(ModuleManager moduleManager, CommandManager commandManager, DatabaseManager databaseManager) {
        this.moduleManager = moduleManager;
        this.commandManager = commandManager;
        this.databaseManager = databaseManager;

        name = "eval";
        type = CommandType.DEBUG;
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @Override
    public void execute(CommandContext context) {
        context.send(CommandIcons.working + "Evaluating...", message -> {
            try {
                engine.put("jda", context.getEvent().getJDA());
                engine.put("context", context);
                engine.put("moduleManager", moduleManager);
                engine.put("commandManager", commandManager);
                engine.put("databaseManager", databaseManager);

                Object o = engine.eval(context.getConcatArgs());
                String e = o == null ? "null" : o.toString();

                if (e.length() > 1990) e = e.substring(1990);
                if (context.checkSensitive(e)) {
                    message.editMessage(CommandIcons.error + context.getTranslated("generic.sensitive")).queue();
                } else {
                    message.editMessage("```\n" + e + "\n```").queue();
                }
            } catch (Exception e) {
                logger.error("Caught exception in eval", e);
                message.editMessage(CommandIcons.error + e.getMessage()).queue();
            }
        });
    }
}
