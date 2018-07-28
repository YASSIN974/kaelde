package moe.kyokobot.bot.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Optional;

public class UserUtil {
    public static Member getMember(Guild guild, String name) {
        return getMember(guild, name, false);
    }

    public static Member getMember(Guild guild, String name, boolean notBot) {
        String arg = name.trim();
        Optional<Member> member = guild.getMembers().stream().parallel().filter(ftr ->
                (ftr.getUser().getAsMention().equals(arg)
                        || ftr.getUser().getName().equalsIgnoreCase(arg)
                        || arg.equals(ftr.getUser().getId())
                        || (ftr.getNickname() != null && arg.equalsIgnoreCase(ftr.getNickname()))
                        || arg.equals("<@" + ftr.getUser().getId() + ">")
                        || arg.equals("<@!" + ftr.getUser().getId() + ">")
                        || arg.equalsIgnoreCase(ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())
                        || arg.equalsIgnoreCase("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())) && (!notBot || !ftr.getUser().isBot())
        ).findFirst();
        return member.orElse(null);
    }

    public static Guild.Ban getBan(Guild guild, String arg) throws PermissionException {
        Optional<Guild.Ban> ban = guild.getBanList().complete().stream().parallel().filter(ftr ->
                ftr.getUser().getAsMention().equals(arg)
                        || ftr.getUser().getName().equalsIgnoreCase(arg)
                        || arg.equals(ftr.getUser().getId())
                        || arg.equals("<@" + ftr.getUser().getId() + ">")
                        || arg.equals("<@!" + ftr.getUser().getId() + ">")
                        || arg.equalsIgnoreCase(ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())
                        || arg.equalsIgnoreCase("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
        return ban.orElse(null);
    }

    public static String toDiscrim(User u) {
        return u.getName() + "#" + u.getDiscriminator();
    }
}
