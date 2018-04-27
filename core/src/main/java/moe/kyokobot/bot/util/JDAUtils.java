package moe.kyokobot.bot.util;

import moe.kyokobot.bot.discordapi.entity.Guild;
import moe.kyokobot.bot.discordapi.entity.Member;
import moe.kyokobot.bot.discordapi.entity.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class JDAUtils {
    public static Member memberFromJDA(Guild guild, net.dv8tion.jda.core.entities.Member member) {
        Member m = new Member();
        m.bot = member.getUser().isBot();
        m.discrim = Short.valueOf(member.getUser().getDiscriminator());
        m.effectiveName = member.getEffectiveName();
        m.name = member.getUser().getName();
        m.guild = guild;
        m.id = member.getUser().getId();
        m.idLong = member.getUser().getIdLong();
        return m;
    }

    public static List<Member> membersFromJDA(Guild guild, List<net.dv8tion.jda.core.entities.Member> members) {
        ArrayList<Member> l = new ArrayList<>();

        members.forEach(member -> {
            l.add(memberFromJDA(guild, member));
        });
        return l;
    }

    public static TextChannel textChannelFromJDA(Guild guild, net.dv8tion.jda.core.entities.TextChannel channel) {
        TextChannel t = new TextChannel();
        t.id = channel.getId();
        t.idLong = channel.getIdLong();
        t.name = channel.getName();
        t.nsfw = channel.isNSFW();
        return t;
    }

    public static List<TextChannel> textChannelsFromJDA(Guild guild, List<net.dv8tion.jda.core.entities.TextChannel> textChannels) {
        ArrayList<TextChannel> l = new ArrayList<>();

        textChannels.forEach(chan -> {
            l.add(textChannelFromJDA(guild, chan));
        });
        return l;
    }

    public static Guild guildFromJDA(net.dv8tion.jda.core.entities.Guild guild) {
        Guild g = new Guild();
        g.idLong = guild.getIdLong();
        g.id = guild.getId();
        g.name = guild.getName();
        g.members = membersFromJDA(g, guild.getMembers());
        g.owner = g.members.stream().filter(member -> member.idLong == guild.getOwner().getUser().getIdLong()).findFirst().get();
        g.textChannels = textChannelsFromJDA(g, guild.getTextChannels());
        return g;
    }
}
