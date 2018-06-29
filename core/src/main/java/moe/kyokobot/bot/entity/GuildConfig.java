package moe.kyokobot.bot.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GuildConfig implements DatabaseEntity {
    public GuildConfig() {

    }

    public GuildConfig(String guildId, Language language, ArrayList<String> prefixes) {
        this.guildId = guildId;
        this.language = language;
        this.prefixes = prefixes;
    }

    @SerializedName("id")
    private String guildId = "";
    private Language language = Language.ENGLISH;
    private ArrayList<String> prefixes = new ArrayList<>();
    @SerializedName("music-config")
    private MusicConfig musicConfig = new MusicConfig();
    private boolean experimental = false;
    @SerializedName("auto-role")
    private String autoRole = "";
    @SerializedName("self-assignable")
    private List<String> selfAssignable = new ArrayList<>();

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