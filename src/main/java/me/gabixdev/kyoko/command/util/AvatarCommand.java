package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

public class AvatarCommand extends Command {
    private final String[] aliases = new String[]{"avatar"};
    private Kyoko kyoko;

    public AvatarCommand(Kyoko kyoko) {
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
        return "avatar.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.UTILITY;
    }

    @Override
    public String getUsage() {
        return "avatar.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());
        User target = null;

        if (args.length != 2) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        for (Member mem : message.getGuild().getMembers()) {
            if (mem.getAsMention().equals(args[1])
                    || mem.getUser().getName().equalsIgnoreCase(args[1])
                    || mem.getEffectiveName().equalsIgnoreCase(args[1])) {

                if (mem.getUser().getAvatarUrl() == null) {
                    EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                    normal.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "avatar.null"), false);
                    message.getTextChannel().sendMessage(normal.build()).queue();
                } else {
                    byte[] data = URLUtil.readUrlBytes(mem.getUser().getAvatarUrl());
                    message.getChannel().sendFile(data, "avatar.png", new MessageBuilder().append(String.format(kyoko.getI18n().get(l, "avatar.user"), mem.getEffectiveName())).build()).queue();
                }
                return;
            }
        }
    }
}
