package me.gabixdev.kyoko.bot.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Optional;

public class UserUtil {
    public static Member getMember(Guild guild, String arg) {
        Optional<Member> member = guild.getMembers().stream().parallel().filter(ftr ->
                ftr.getUser().getAsMention().equals(arg)
                        || arg.equalsIgnoreCase(ftr.getUser().getName())
                        || arg.equals(ftr.getUser().getId())
                        || arg.equalsIgnoreCase((ftr.getNickname() == null ? "" : ftr.getNickname()))
                        || arg.equals("<@!" + ftr.getUser().getId() + ">")
                        || arg.equalsIgnoreCase(ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())
                        || arg.equals("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
        return member.orElse(null);
    }

    public static User getBannedUser(Guild guild, String arg) throws PermissionException {
        Optional<Guild.Ban> ban = guild.getBanList().complete().stream().parallel().filter(ftr ->
                ftr.getUser().getAsMention().equals(arg)
                        || ftr.getUser().getName().equalsIgnoreCase(arg)
                        || arg.equals(ftr.getUser().getId())
                        || arg.equals("<@!" + ftr.getUser().getId() + ">")
                        || arg.equals("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
        return ban.map(Guild.Ban::getUser).orElse(null);
    }
}
