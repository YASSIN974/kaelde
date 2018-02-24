package me.gabixdev.kyoko.command.moderation;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BanCommand extends Command
{
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"ban"};
    public BanCommand(Kyoko kyoko)
    {
        this.kyoko = kyoko;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getUsage() {
        return "mod.ban.usage";
    }

    @Override
    public String getDescription() {
        return "mod.ban.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable
    {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if(args.length < 2)
        {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }
        if(message.getMember().hasPermission(Permission.BAN_MEMBERS))
        {
            try {
                String reason = "none";
                if(args.length > 2) reason = Arrays.stream(args).skip(2).collect(Collectors.joining(" "));
                Member member = UserUtil.getMember(kyoko, l, message.getTextChannel(), args[1]);
                if(member == null) return;
                Message msg = new MessageBuilder().append(String.format(kyoko.getI18n().get(l, "mod.ban.banned"), message.getMember().getAsMention(), member.getAsMention()))
                        .append("\n").append(String.format(kyoko.getI18n().get(l, "mod.ban.reason"), reason)).build();
                message.getTextChannel().sendMessage(msg).queue();
                message.getGuild().getController().ban(member, 0, reason).queue();
            } catch (PermissionException e) {
                CommonErrorUtil.noPermissionBot(kyoko, l, message.getTextChannel());
            }
        } else {
            CommonErrorUtil.noPermissionUser(kyoko, l, message.getTextChannel());
        }
    }
}
