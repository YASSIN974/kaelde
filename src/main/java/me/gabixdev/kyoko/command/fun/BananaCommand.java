package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BananaCommand extends Command {
    //private Random rng;

    private final String[] aliases = new String[]{"banana", "mujbanan"};
    private Kyoko kyoko;

    public BananaCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        //this.rng = new Random();
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
        return "banana.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public String getUsage() {
        return "banana.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());
        /*if (!message.getTextChannel().isNSFW()) {
            printNSFW(kyoko, l, message.getTextChannel());
            return;
        }*/

        User u = message.getAuthor();
        EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        if (args.length == 1) {
            builder.setTitle(String.format(kyoko.getI18n().get(l, "banana.yours"), 9 + u.getIdLong() % 20));
        } else if (args.length >= 2) {
            boolean skipme = false;

            if (message.getRawContent().startsWith(kyoko.getJda().getSelfUser().getAsMention())) {
                if (StringUtil.getOccurencies(message.getRawContent(), kyoko.getJda().getSelfUser().getAsMention()) == 1)
                    skipme = true;
            }

            if (message.getMentionedUsers().isEmpty()) {
                String str = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
                builder.setTitle(String.format(kyoko.getI18n().get(l, "banana.else"), str, Math.abs(9 + str.hashCode() % 20)));
            } else {
                if (message.getMentionedUsers().size() >= 1) {
                    for (User us : message.getMentionedUsers()) {
                        if (skipme)
                            if (u.getIdLong() == kyoko.getJda().getSelfUser().getIdLong())
                                continue;
                        u = us;
                        break;
                    }
                    builder.setTitle(String.format(kyoko.getI18n().get(l, "banana.else"), u.getName(), 9 + u.getIdLong() % 20));
                } else {
                    if (skipme) {
                        String str = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
                        builder.setTitle(String.format(kyoko.getI18n().get(l, "banana.else"), str, Math.abs(9 + str.hashCode() % 20)));
                    } else {
                        u = kyoko.getJda().getSelfUser();
                        builder.setTitle(String.format(kyoko.getI18n().get(l, "banana.else"), u.getName(), 9 + u.getIdLong() % 20));
                    }
                }

            }
        }

        builder.setImage("https://gabixdev.me/kyoko/api/banan.png");
        message.getChannel().sendMessage(builder.build()).queue();
    }
}
