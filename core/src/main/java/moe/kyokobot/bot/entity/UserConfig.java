package moe.kyokobot.bot.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserConfig implements DatabaseEntity {
    public UserConfig() {

    }

    public UserConfig(String id) {
        this.id = id;
    }

    private String id = "";
    private Language language = Language.DEFAULT;
    private long level = 1L;
    private long money = 0L;
    private long xp = 0L;
    private long claim = 0L;
    private long reputation = 0L;
    private long voted = 0L;
    private int theme = 1;
    @SerializedName("no-dms")
    private boolean noDMs = false;
    private String image = "default";
    @SerializedName("kv-store")
    private Map<String, String> kvStore = new HashMap<>();
    @SerializedName("list-store")
    private Map<String, ArrayList<String>> listStore = new HashMap<>();
    private List<Item> items = new ArrayList<>();
    @SerializedName("action-stats")
    private ActionStats actionStats;

    @Getter
    @Setter
    public class ActionStats {
        private int pats;
        private int hugs;
    }

    @Transient
    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    public String toString() {
        return GsonUtil.toJSON(this);
    }
}