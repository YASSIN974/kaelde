package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class SpinnerCommand extends Command {
    private final String gifurl = "https://wersm.com/wp-content/uploads/2017/05/wersm-fidget-spinner.gif";

    private final String[] aliases = new String[]{"spinner", "fidgetspinner", "fidget", "autism"};
    private Kyoko kyoko;

    public SpinnerCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "spinner.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        normal.setTitle(kyoko.getI18n().get(l, "spinner.title"));
        normal.setImage(gifurl);
        message.getTextChannel().sendMessage(normal.build()).queue();
    }
}
