package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CharCodes;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class BannerCommand extends Command {
    private final String[] aliases = new String[]{"banner", "bigtext"};
    private Kyoko kyoko;

    public BannerCommand(Kyoko kyoko) {
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
        return "banner.description";
    }

    @Override
    public String getUsage() {
        return "banner.usage";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        StringBuilder builder = new StringBuilder();
        if (args.length == 1) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        for (int i = 1; i < args.length; i++) {
            for (char c : args[i].toLowerCase().toCharArray()) {
                builder.append(toRegionalIndicator(c)).append(" ");
            }
            builder.append("   ");
        }

        message.getTextChannel().sendMessage(builder.toString()).queue();
    }

    private String toRegionalIndicator(char c) {
        if (c >= CharCodes.SMALL_A && c <= CharCodes.SMALL_Z) {
            c -= CharCodes.SMALL_A;
            return String.valueOf(Character.toChars(CharCodes.REGIONAL_INDICATOR_A + c));
        } else {
            return "";
        }
    }
}
