package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

/*
 * @author Marvin W. (NurMarvin)
 * @date 24.02.2018
 */

public class KysCommand extends Command {

    private final String[] aliases = new String[]{"s", "kys", "killyourself"};
    private Kyoko kyoko;

    public KysCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    @Override
    public String getLabel() {
        return "ky!s";
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "kys.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        Member member = null;
        if (args.length != 1) {
            String name = message.getContentRaw().substring(args[0].length() + kyoko.getSettings().getPrefix().length() + 1);
            member = UserUtil.getMember(message.getGuild(), name);
            if (member == null) {
                CommonErrorUtil.noUserFound(kyoko, l, message.getTextChannel(), name);
                return;
            }
        } else member = message.getMember();
        message.getTextChannel().sendMessage(String.format(kyoko.getI18n().get(l, "kys.message"), member.getAsMention())).queue();
    }
}
