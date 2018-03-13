package me.gabixdev.kyoko.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import me.gabixdev.kyoko.i18n.Language;

@DatabaseTable(tableName = "users")
public class UserConfig {

    public UserConfig() {
    }

    public UserConfig(Long userId, Language language, Integer level, Integer money, Integer xp, Long claim) {
        this.userId = userId;
        this.language = language;
        this.level = level;
        this.money = money;
        this.xp = xp;
        this.claim = claim;
    }

    @DatabaseField(id = true)
    public Integer id;


    @DatabaseField(columnName = "userid")
    public Long userId;
    @DatabaseField(columnName = "lang")
    public Language language;
    @DatabaseField
    public int level;
    @DatabaseField
    public Integer money;
    @DatabaseField
    public Integer xp;
    @DatabaseField
    public Long claim;

    public String toString() {
        return id + " " + userId + " " + language + " " + level + " " + money + " " + xp + " " + claim;
    }
}