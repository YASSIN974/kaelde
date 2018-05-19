package moe.kyokobot.bot.command.debug;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EvalCommand extends Command {
    private ScriptEngine engine;

    public EvalCommand() {
        name = "eval";
        type = CommandType.DEBUG;
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    @Override
    public void execute(CommandContext context) {
        context.send(context.working() + "Evaluating...", message -> {
            try {
                engine.put("jda", context.getEvent().getJDA());
                engine.put("context", context);
                String e = engine.eval(context.getConcatArgs()).toString();
                if (e.length() > 1990) e = e.substring(1990);
                if (context.checkSensitive(e)) {
                    message.editMessage(context.error() + context.getTranslated("generic.sensitive")).queue();
                } else {
                    message.editMessage("```\n" + e + "\n```").queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.editMessage(context.error() + e.getMessage()).queue();
            }
        });
    }
}
