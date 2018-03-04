package me.gabixdev.kyoko.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.Optional;

public class UserUtil {
    public static Member getMember(Guild guild, String arg) {
        Optional<Member> member = guild.getMembers().stream().parallel().filter(ftr ->
                ftr.getUser().getAsMention().equals(arg)
                        || ftr.getUser().getName().equalsIgnoreCase(arg)
                        || ftr.getNickname().equalsIgnoreCase(arg)
                        || arg.equals(ftr.getUser().getId())
                        || arg.equals("<@!" + ftr.getUser().getId() + ">")
                        || arg.equals("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
        if (!member.isPresent()) {
            //CommonErrorUtil.noUserFound(kyoko, language, channel, arg); // NO!
            return null;
        }

        return member.get();
    }

    public static User getBannedUser(Guild guild, String arg) throws PermissionException {
        //try {
        Optional<Guild.Ban> ban = guild.getBanList().complete().stream().parallel().filter(ftr ->
                ftr.getUser().getAsMention().equals(arg)
                        || ftr.getUser().getName().equalsIgnoreCase(arg)
                        || arg.equals(ftr.getUser().getId())
                        || arg.equals("<@!" + ftr.getUser().getId() + ">")
                        || arg.equals("@" + ftr.getUser().getName() + "#" + ftr.getUser().getDiscriminator())).findFirst();
        if (!ban.isPresent()) {
            //CommonErrorUtil.noBanFound(kyoko, language, channel, arg);
            return null;
        }
        return ban.get().getUser();
        /*} catch (PermissionException e) {
            CommonErrorUtil.noPermissionBot(kyoko, language, channel);
            return null;
        }*/
    }

}
