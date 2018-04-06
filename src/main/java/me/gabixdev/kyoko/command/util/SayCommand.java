package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class SayCommand extends Command {
    private final String[] aliases = new String[]{"say", "print"};
    private Kyoko kyoko;

    public SayCommand(Kyoko kyoko) {
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
        return "say.description";
    }

    @Override
    public String getUsage() {
        return "say.usage";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILITY;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        String msg = message.getContentRaw();
        String mention = kyoko.getJda().getSelfUser().getAsMention();

        //if (!message.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
            msg = msg.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere");
        //}

        if (msg.startsWith(mention)) {
            msg = msg.substring(mention.length()).trim().substring(args[0].length());
        } else {
            msg = msg.substring(kyoko.getSettings().getPrefix().length() + args[0].length());
        }

        if (msg.trim().isEmpty()) {
            printUsage(kyoko, kyoko.getI18n().getLanguage(message.getMember()), message.getTextChannel());
            return;
        }

        message.getTextChannel().sendMessage(msg).queue();
    }
}
