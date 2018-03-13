package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CharCodes;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
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
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        StringBuilder builder = new StringBuilder();
        if (args.length == 1) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        for (int i = 1; i < args.length; i++) {
            args[i] = args[i].replace("✔️", "✔").replace("✅", "✔").replace("☑️", "✔");
            for (char c : args[i].toLowerCase().toCharArray()) {
                builder.append(toRegionalIndicator(c)).append(" ");
            }
            builder.append("   ");
        }

        message.getTextChannel().sendMessage(builder.toString()).queue();
    }

    private String toRegionalIndicator(char c) {
        if (c == '❤') return "\uD83D\uDC9F";
        if (c == '✔') return "✅";

        if (c >= CharCodes.SMALL_A && c <= CharCodes.SMALL_Z) {
            c -= CharCodes.SMALL_A;
            return String.valueOf(Character.toChars(CharCodes.REGIONAL_INDICATOR_A + c));
        } else if (c >= CharCodes.NUM_0 && c <= CharCodes.NUM_9) {
            return new String(new char[]{c, '\u20E3'});
        } else {
            return "";
        }
    }
}
