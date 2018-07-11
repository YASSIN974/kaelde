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
    @SuppressWarnings("unused")
    public GuildConfig() {

    }

    @SuppressWarnings("squid:S00107")
    public GuildConfig(String guildId, Language language, ArrayList<String> prefixes) {
        this.guildId = guildId;
        this.language = language;
        this.prefixes = prefixes;
    }

    @SerializedName("id")
    private String guildId = "";
    private Language language = Language.ENGLISH;
    private ArrayList<String> prefixes = new ArrayList<>();
    @SerializedName("moderation-config")
    private ModerationConfig moderationConfig = new ModerationConfig();
    @SerializedName("music-config")
    private MusicConfig musicConfig = new MusicConfig();
    private boolean experimental = false;

    @Transient
    @Override
    public String getTableName() {
        return "guilds";
    }

    @Override
    public String toString() {
        return GsonUtil.toJSON(this);
    }

    @Getter
    @Setter
    public class MusicConfig {
        @SuppressWarnings({"unused", "WeakerAccess"})
        public MusicConfig() {
            // default
        }

        @SerializedName("dj-role")
        private String djRole = "";
    }

    @Getter
    @Setter
    public class ModerationConfig {
        @SuppressWarnings({"unused", "WeakerAccess"})
        public ModerationConfig() {
            // default
        }

        @SerializedName("levelup-messages")
        private boolean levelupMessages = false;

        @SerializedName("auto-role")
        private String autoRole = "";

        @SerializedName("self-assignable")
        private List<String> selfAssignable = new ArrayList<>();
    }
}