package moe.kyokobot.bot.discordapi.entity;

import java.util.List;

public class Guild {
    public String id;
    public long idLong;
    public String name;
    public Member owner;
    public List<TextChannel> textChannels;
    public List<VoiceChannel> voiceChannels;
    public List<Member> members;
}
