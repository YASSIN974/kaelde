package me.gabixdev.kyoko.command.moderation;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PrefixCommand extends Command {
    private final Kyoko kyoko;

    public PrefixCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.aliases = new String[]{"prefix", "prefixes"};;
        this.label = aliases[0];
        this.description = "prefix.description";
        this.usage = "prefix.usage";
        this.category = CommandCategory.MODERATION;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        String def = kyoko.getSettings().getPrefix();

        if (args.length == 1 || (args.length > 1 && args[1].toLowerCase().equalsIgnoreCase("list"))) {
            StringBuilder sb = new StringBuilder();

            List<String> prefixes = kyoko.getCommandManager().getPrefixes(message.getGuild());
            if (prefixes.size() == 0) {
                sb.append(String.format(kyoko.getI18n().get(l, "prefixes.none"), def)).append("\n\n");
            } else {
                for (int i = 0; i < prefixes.size(); i++) {
                    sb.append(i + 1).append(". `").append(prefixes.get(i)).append("`\n");
                }
                sb.append("\n\n");
            }

            sb.append(String.format(kyoko.getI18n().get(l, "prefixes.bottom"), def, def));

            eb.addField(kyoko.getI18n().get(l, "prefixes.top"), sb.toString(), false);
            message.getTextChannel().sendMessage(eb.build()).queue();
        } else {
            if (!message.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                CommonErrorUtil.noPermissionUser(kyoko, l, message.getTextChannel());
                return;
            }

            switch (args[1].toLowerCase()) {
                case "add":
                    String msg = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));

                    if ((msg.length() > 1) && (msg.startsWith("\"") && msg.endsWith("\"") || (msg.startsWith("'") && msg.endsWith("'")) || (msg.startsWith("`") && msg.endsWith("`"))))
                        msg = msg.substring(1, msg.length() - 1);

                    if (msg.trim().isEmpty() || msg.startsWith(" ") || msg.startsWith("\t") || msg.startsWith("\u200b")) {
                        eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "prefixes.noempty"), false);
                        message.getTextChannel().sendMessage(eb.build()).queue();
                        break;
                    }

                    if (msg.length() > 32) {
                        eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "prefixes.toolong"), false);
                        message.getTextChannel().sendMessage(eb.build()).queue();
                        break;
                    }

                    LinkedList<String> prefixes = new LinkedList<String>(kyoko.getCommandManager().getPrefixes(message.getGuild()));
                    if (prefixes.size() == 10) {
                        eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "prefixes.toomany"), false);
                        message.getTextChannel().sendMessage(eb.build()).queue();
                        break;
                    }

                    if (msg.equalsIgnoreCase(kyoko.getSettings().getPrefix()) || prefixes.contains(msg)) {
                        eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "prefixes.exists"), false);
                        message.getTextChannel().sendMessage(eb.build()).queue();
                        break;
                    }
                    prefixes.add(msg.toLowerCase());
                    kyoko.getCommandManager().setPrefixes(message.getGuild(), prefixes);

                    eb.addField(kyoko.getI18n().get(l, "prefixes.title"), String.format(kyoko.getI18n().get(l, "prefixes.added"), msg), false);
                    message.getTextChannel().sendMessage(eb.build()).queue();
                    break;
                case "remove":
                    int num;
                    try {
                        num = Integer.parseUnsignedInt(args[2]);
                    } catch (NumberFormatException e) {
                        CommonErrorUtil.notANumber(kyoko, l, message.getTextChannel(), args[2]);
                        break;
                    }

                    LinkedList<String> prefixes2 = new LinkedList<String>(kyoko.getCommandManager().getPrefixes(message.getGuild()));

                    if (num < 1 || num > (prefixes2.size())) {
                        eb = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        eb.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "prefixes.outrange"), false);
                        message.getTextChannel().sendMessage(eb.build()).queue();
                    } else {
                        String removed = prefixes2.remove(num - 1);
                        kyoko.getCommandManager().setPrefixes(message.getGuild(), prefixes2);

                        eb.addField(kyoko.getI18n().get(l, "prefixes.title"), String.format(kyoko.getI18n().get(l, "prefixes.removed"), removed), false);
                        message.getTextChannel().sendMessage(eb.build()).queue();
                    }
                    break;
                default:
                    printUsage(kyoko, l, message.getTextChannel());
                    break;
            }
        }
    }
}
