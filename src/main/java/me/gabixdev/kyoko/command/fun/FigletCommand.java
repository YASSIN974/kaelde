package me.gabixdev.kyoko.command.fun;

import com.github.lalyos.jfiglet.FigletFont;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FigletCommand extends Command {
    private List<String> fontList;
    private String cachedList;
    private final String[] aliases = new String[]{"figlet"};
    private Kyoko kyoko;

    public FigletCommand(Kyoko kyoko) {
        this.kyoko = kyoko;

        this.fontList = new ArrayList<>();

        //fontList.add("banner"); // BROKEN!
        fontList.add("big");
        fontList.add("block");
        fontList.add("bubble");
        fontList.add("digital");
        fontList.add("ivrit");
        fontList.add("lean");
        fontList.add("mini");
        fontList.add("script");
        fontList.add("shadow");
        fontList.add("slant");
        fontList.add("small");
        fontList.add("smscript");
        fontList.add("smshadow");
        fontList.add("smslant");
        fontList.add("standard");
        fontList.add("term");

        cachedList = "`" + String.join("`, `", fontList) + "`";
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
        return "figlet.description";
    }

    @Override
    public String getUsage() {
        return "figlet.usage";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        if (args.length == 1) {
            printUsage(kyoko, kyoko.getI18n().getLanguage(message.getMember()), message.getTextChannel());
            return;
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("list")) {
                // print list
                EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                Language l = kyoko.getI18n().getLanguage(message.getMember());
                normal.addField(kyoko.getI18n().get(l, "figlet.msg.list"), cachedList, false);
                message.getChannel().sendMessage(normal.build()).queue();
                return;
            } else {
                printUsage(kyoko, kyoko.getI18n().getLanguage(message.getMember()), message.getTextChannel());
                return;
            }
        } else {
            if (!fontList.contains(args[1].toLowerCase())) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                Language l = kyoko.getI18n().getLanguage(message.getMember());
                err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "figlet.msg.unknownfont"), kyoko.getSettings().getPrefix()), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            }

            String msg = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

            if (msg.trim().isEmpty()) {
                printUsage(kyoko, kyoko.getI18n().getLanguage(message.getMember()), message.getTextChannel());
                return;
            }

            String gened = new StringBuilder("```\n").append(FigletFont.convertOneLine(getClass().getResourceAsStream(new StringBuilder("/figlet/").append(args[1].toLowerCase()).append(".flf").toString()), msg)).append("\n```").toString();

            if (gened.length() > 2000) {
                EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                Language l = kyoko.getI18n().getLanguage(message.getMember());
                err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "figlet.msg.toolong"), false);
                message.getChannel().sendMessage(err.build()).queue();
                return;
            } else {
                message.getTextChannel().sendMessage(gened).queue();
            }
        }
    }
}
