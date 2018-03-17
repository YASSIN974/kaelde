package me.gabixdev.kyoko.command.moderation;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UnbanCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"unban"};

    public UnbanCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String getDescription() {
        return "mod.unban.description";
    }

    @Override
    public String getUsage() {
        return "mod.unban.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        if (args.length < 2) {
            printUsage(kyoko, l, message.getTextChannel());
        } else {
            if (message.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                try {
                    String username = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
                    User banned = UserUtil.getBannedUser(message.getGuild(), username);
                    if (banned == null) {
                        CommonErrorUtil.noBanFound(kyoko, l, message.getTextChannel(), args[1]);
                        return;
                    }
                    message.getGuild().getController().unban(banned).queue();
                    Message msg = new MessageBuilder().append(String.format(kyoko.getI18n().get(l, "mod.unban.unbanned"), message.getMember().getAsMention(), banned.getAsMention())).build();
                    message.getTextChannel().sendMessage(msg).queue();

                } catch (PermissionException e) {
                    CommonErrorUtil.noPermissionBot(kyoko, l, message.getTextChannel());
                }
            } else {
                CommonErrorUtil.noPermissionUser(kyoko, l, message.getTextChannel());
            }
        }
    }
}
