package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

import java.util.Optional;

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
    public CommandType getType() {
        return CommandType.UTILITY;
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

        Optional<Member> member = message.getGuild().getMembers().stream().parallel().filter(
                mem -> mem.getAsMention().equals(args[1])
                        || mem.getUser().getName().equalsIgnoreCase(args[1])
                        || mem.getEffectiveName().equalsIgnoreCase(args[1])).findFirst();

        if (member.isPresent()) {
            Member mem = member.get();
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

