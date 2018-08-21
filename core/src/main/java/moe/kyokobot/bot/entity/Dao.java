package moe.kyokobot.bot.entity;

import moe.kyokobot.bot.manager.DatabaseManager;

public class Dao<T extends DatabaseEntity> {
    private final DatabaseManager databaseManager;

    protected Dao(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public T get(String id) {
        return null;
    }
}
