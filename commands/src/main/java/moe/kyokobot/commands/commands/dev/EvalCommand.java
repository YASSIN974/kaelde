package moe.kyokobot.commands.commands.dev;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import moe.kyokobot.bot.command.CommandType;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.manager.DatabaseManager;
import moe.kyokobot.bot.manager.ModuleManager;
import net.dv8tion.jda.bot.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStreamReader;
import java.io.Reader;

public class EvalCommand extends Command {
    private final Logger logger = LoggerFactory.getLogger(EvalCommand.class);
    private final ShardManager shardManager;
    private final ModuleManager moduleManager;
    private final CommandManager commandManager;
    private final DatabaseManager databaseManager;

    private ScriptEngine engine;
    private boolean babelEnabled;

    public EvalCommand(ShardManager shardManager, ModuleManager moduleManager, CommandManager commandManager, DatabaseManager databaseManager) {
        this.shardManager = shardManager;
        this.moduleManager = moduleManager;
        this.commandManager = commandManager;
        this.databaseManager = databaseManager;

        name = "eval";
        type = CommandType.DEBUG;
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        context.send(CommandIcons.WORKING + "Evaluating...", message -> {
            try {
                if (engine == null)
                    setupEngine();

                if (shardManager != null)
                    engine.put("shardManager", shardManager);
                engine.put("logger", logger);
                engine.put("jda", context.getEvent().getJDA());
                engine.put("context", context);
                engine.put("moduleManager", moduleManager);
                engine.put("commandManager", commandManager);
                engine.put("databaseManager", databaseManager);

                Object o;
                if (babelEnabled) {
                    engine.put("input", context.getConcatArgs());
                    String s = engine.eval("Babel.transform(input, { presets: ['es2015'] }).code").toString();
                    o = engine.eval(s);
                } else {
                    o = engine.eval(context.getConcatArgs());
                }

                String e = o == null ? "null" : o.toString();
                if (babelEnabled && e.equals("use strict")) e = "null";

                if (e.length() > 1990) e = e.substring(1990);

                if (context.checkSensitive(e)) {
                    message.editMessage(CommandIcons.ERROR + context.getTranslated("generic.sensitive")).queue();
                } else {
                    message.editMessage("```\n" + e + "\n```").queue();
                }
            } catch (Exception e) {
                logger.error("Caught exception in eval", e);
                message.editMessage(CommandIcons.ERROR + e.getMessage()).queue();
            }
        });
    }

    private void setupEngine() {
        engine = new ScriptEngineManager().getEngineByName("JavaScript");

        if (getClass().getResource("/babel.min.js") != null) {
            logger.info("Loading Babel...");

            try (Reader r = new InputStreamReader(getClass().getResourceAsStream("/babel.min.js"))) {
                engine.put("logger", logger);
                CompiledScript compiled = ((Compilable) engine).compile(r);
                compiled.eval();
                babelEnabled = true;
            } catch (Exception e) {
                logger.error("Error loading Babel!", e);
            }
        }
    }
}
