package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.remoteapi.ResponseException;
import me.gabixdev.kyoko.remoteapi.kiciusie.KiciusieMode;
import me.gabixdev.kyoko.remoteapi.kiciusie.KiciusieResponse;
import me.gabixdev.kyoko.remoteapi.kiciusie.KiciusieResponses;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class CatCommand extends Command {
    private final KiciusieResponses kiciusieResponses = new KiciusieResponses();

    private final String[] aliases = new String[]{"cat"};
    private Kyoko kyoko;

    public CatCommand(Kyoko kyoko) {
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
        return "cat.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public String getUsage() {
        return "cat.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());
        KiciusieMode mode;

        if (args.length == 1) {
            mode = KiciusieMode.RANDOM;
        } else if (args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "random":
                    mode = KiciusieMode.RANDOM;
                    break;
                case "image":
                    mode = KiciusieMode.IMAGE;
                    break;
                case "gif":
                    mode = KiciusieMode.GIF;
                    break;
                default:
                    printUsage(kyoko, l, message.getTextChannel());
                    return;
            }
        } else {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }


        try {
            KiciusieResponse response = kiciusieResponses.call(mode);
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            builder.addField(kyoko.getI18n().get(l, "cat.title"), kyoko.getI18n().get(l, "cat.subtitle"), true);
            builder.setImage(response.getImageUrl());
            message.getChannel().sendMessage(builder.build()).queue();
        } catch (ResponseException ex) {
            EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            builder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "cat.error"), false);
            message.getChannel().sendMessage(builder.build()).queue();
            ex.printStackTrace();
        }
    }
}
