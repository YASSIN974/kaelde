package me.gabixdev.kyoko.bot.command.basic;

import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;

public class HelpCommand extends Command {
    public HelpCommand(Kyoko kyoko) {
        this.name = "help";
        this.aliases = new String[]{"?"};
        this.description = "help.description";
        this.category = CommandCategory.BASIC;
    }

    @Override
    public void execute(CommandContext context) {
        context.getChannel().sendMessage("test blokady").queue();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        context.getChannel().sendMessage("ok").queue();
    }
}
