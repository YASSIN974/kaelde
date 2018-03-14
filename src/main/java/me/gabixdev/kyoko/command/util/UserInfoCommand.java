package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserInfoCommand extends Command {
    private final String[] aliases = new String[]{"userinfo"};
    private Kyoko kyoko;

    public UserInfoCommand(Kyoko kyoko) {
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
        return "userinfo.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UTILITY;
    }

    @Override
    public String getUsage() {
        return "userinfo.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        User target = null;

        if (args.length != 2) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        String username = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        Member mem = UserUtil.getMember(message.getGuild(), username);
        if (mem != null) {
            EmbedBuilder normal = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            if (mem.getUser().getAvatarUrl() != null) normal.setThumbnail(mem.getUser().getAvatarUrl());
            StringBuilder desc = new StringBuilder();
            desc.append(kyoko.getI18n().get(l, "userinfo.tag")).append(": `").append(mem.getUser().getName()).append("#").append(mem.getUser().getDiscriminator()).append("`\n");
            desc.append(kyoko.getI18n().get(l, "userinfo.id")).append(": `").append(mem.getUser().getId()).append("`");
            normal.addField(kyoko.getI18n().get(l, "userinfo.title"), desc.toString(), false);
            message.getTextChannel().sendMessage(normal.build()).queue();
        } else {
            CommonErrorUtil.noUserFound(kyoko, l, message.getTextChannel(), args[1]);
        }

    }
}

