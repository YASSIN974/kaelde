package me.gabixdev.kyoko.bot.command.util;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;

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
            context.send(context.getErrorEmbed().addField("Error", "Message is empty!", false).build());
        } else {
            context.send(context.getConcatArgs());
        }
    }
}
