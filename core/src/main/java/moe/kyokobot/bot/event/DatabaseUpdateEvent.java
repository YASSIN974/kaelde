package moe.kyokobot.bot.event;

import lombok.Getter;
import moe.kyokobot.bot.entity.DatabaseEntity;

@Getter
public class DatabaseUpdateEvent {
    private final DatabaseEntity entity;

    public DatabaseUpdateEvent(DatabaseEntity entity) {
        this.entity = entity;
    }
}
