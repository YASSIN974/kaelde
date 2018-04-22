package moe.kyokobot.bot.entity;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import moe.kyokobot.bot.i18n.Language;

@DatabaseTable(tableName = "guilds")
public class GuildConfig {

    public GuildConfig() {
    }

    public GuildConfig(Long guildId, Language language, String prefixes) {
        this.guildId = guildId;
        this.language = language;
        this.prefixes = prefixes;
    }

    @DatabaseField(id = true)
    public Integer id;

    @DatabaseField(columnName = "guildid")
    public Long guildId;
    @DatabaseField(columnName = "language")
    public Language language;
    @DatabaseField(columnName = "prefixes")
    public String prefixes;


    public String toString() {
        return id + " " + guildId + " " + language + " " + prefixes;
    }
}