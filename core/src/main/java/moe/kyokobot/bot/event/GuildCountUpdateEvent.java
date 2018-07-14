package moe.kyokobot.bot.event;

import lombok.Getter;

@Getter
public class GuildCountUpdateEvent {
    private final int guildCount;

    public GuildCountUpdateEvent(int guildCount) {
        this.guildCount = guildCount;
    }
}
