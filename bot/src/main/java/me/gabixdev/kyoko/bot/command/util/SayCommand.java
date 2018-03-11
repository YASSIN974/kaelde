package me.gabixdev.kyoko.bot.command.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.CommonErrors;

public class SayCommand extends Command {
    private final Kyoko kyoko;

    public SayCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "say";
        this.category = CommandCategory.UTILITY;
        this.description = "say.description";
        this.usage = "say.usage";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty()) {
            CommonErrors.usage(context);
        } else {
            context.send(context.getConcatArgs());
        }
    }
}
