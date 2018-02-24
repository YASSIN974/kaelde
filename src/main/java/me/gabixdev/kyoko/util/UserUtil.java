package me.gabixdev.kyoko.util;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Optional;

public class UserUtil
{
    public static Member getMember(Kyoko kyoko, Language language, TextChannel channel , String arg)
    {
        Guild guild = channel.getGuild();
        Optional<Member> member = guild.getMembers().stream().parallel().filter(ftr ->
        ftr.getAsMention().equals(arg)
        || ftr.getUser().getName().equalsIgnoreCase(arg)
        || StringUtil.equalsID(arg, ftr.getUser().getIdLong())
        || arg.equals("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
        if(!member.isPresent())
        {
            CommonErrorUtil.noUserFound(kyoko, language, channel, arg);
            return null;
        }

        return member.get();
    }
    public static User getBannedUser(Kyoko kyoko, Language language, TextChannel channel, String arg) {
        Guild guild = channel.getGuild();
        try {
            Optional<Guild.Ban> ban = guild.getBanList().complete().stream().parallel().filter(ftr ->
                    ftr.getUser().getAsMention().equals(arg)
                            || ftr.getUser().getName().equalsIgnoreCase(arg)
                            || StringUtil.equalsID(arg, ftr.getUser().getIdLong())
                            || arg.equals("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
            if (!ban.isPresent()) {
                CommonErrorUtil.noBanFound(kyoko, language, channel, arg);
                return null;
            }
            return ban.get().getUser();
        } catch (PermissionException e) {
            CommonErrorUtil.noPermissionBot(kyoko, language, channel);
            return null;
        }
    }

}
