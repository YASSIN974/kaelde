package moe.kyokobot.bot.event;

public class GuildCountUpdateEvent {
    private final int guildCount;

    public GuildCountUpdateEvent(int guildCount) {
        this.guildCount = guildCount;
    }

    public int getGuildCount() {
        return guildCount;
    }
}
