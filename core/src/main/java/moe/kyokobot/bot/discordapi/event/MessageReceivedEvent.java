package moe.kyokobot.bot.discordapi.event;

import moe.kyokobot.bot.discordapi.DiscordAPI;
import moe.kyokobot.bot.discordapi.entity.Guild;
import moe.kyokobot.bot.discordapi.entity.Member;
import moe.kyokobot.bot.discordapi.entity.TextChannel;
import moe.kyokobot.bot.discordapi.entity.User;

public class MessageReceivedEvent {
    private final DiscordAPI discordAPI;
    private final User user;
    private final Member sender;
    private final TextChannel channel;
    private final Guild guild;
    private final String content;
    private final boolean bot;

    public MessageReceivedEvent(DiscordAPI discordAPI, User user, Member sender, TextChannel channel, Guild guild, String content, boolean bot) {
        this.discordAPI = discordAPI;
        this.user = user;
        this.sender = sender;
        this.channel = channel;
        this.guild = guild;
        this.content = content;
        this.bot = bot;
    }

    public DiscordAPI getDiscordAPI() {
        return discordAPI;
    }

    public User getUser() {
        return user;
    }

    public Member getSender() {
        return sender;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getContent() {
        return content;
    }

    public boolean isBot() {
        return bot;
    }
}
