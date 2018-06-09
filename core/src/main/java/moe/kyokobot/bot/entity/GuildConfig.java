package moe.kyokobot.bot.entity;

import com.google.gson.annotations.SerializedName;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;

public class GuildConfig implements DatabaseEntity {
    public GuildConfig(String guildId, Language language, ArrayList<String> prefixes) {
        this.guildId = guildId;
        this.language = language;
        this.prefixes = prefixes;
    }

    @SerializedName("guild-id")
    public String guildId = "";
    public Language language = Language.ENGLISH;
    public ArrayList<String> prefixes = new ArrayList<>();
    public MusicConfig musicConfig = new MusicConfig();

    @Transient
    @Override
    public String getTableName() {
        return "guilds";
    }

    @Override
    public String toString() {
        return GsonUtil.toJSON(this);
    }

    public class MusicConfig {
        @SerializedName("dj-role")
        public String djRole = "";
    }
}