package moe.kyokobot.bot.entity;


import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;

public class GuildConfig implements DatabaseEntity {
    public GuildConfig(Long guildId, Language language, ArrayList<String> prefixes) {
        this.guildId = guildId;
        this.language = language;
        this.prefixes = prefixes;
    }

    public Long guildId;
    public Language language;
    public ArrayList<String> prefixes;

    @Transient
    @Override
    public String getTableName() {
        return "guilds";
    }

    @Override
    public String toString() {
        return GsonUtil.toJSON(this);
    }
}