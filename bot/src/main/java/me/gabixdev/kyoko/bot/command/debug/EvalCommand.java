package me.gabixdev.kyoko.bot.command.debug;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.command.CommandType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends Command {
    private final Kyoko kyoko;
    private ScriptEngine scriptEngine;

    public EvalCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "eval";
        this.category = CommandCategory.UTILITY;
        this.type = CommandType.DEBUG;

        scriptEngine = new ScriptEngineManager().getEngineByName("js");
        try {
            scriptEngine.eval("load(\"nashorn:mozilla_compat.js\");importPackage(java.lang);importPackage(java.io);importPackage(java.util);");
            scriptEngine.put("kyoko", kyoko);
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void execute(CommandContext context) {
        try {
            scriptEngine.put("context", context);
            Object out = scriptEngine.eval(context.getConcatArgs());
            if (out == null) {
                context.send("Null output");
            } else {
                String dat = out.toString();
                dat = dat.replace(kyoko.getSettings().token, "[censored]");
                context.send("```\n" + dat + "\n```");
            }
        } catch (Exception ex) {
            context.send("**Error:** " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
