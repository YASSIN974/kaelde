package moe.kyokobot.bot.entity;

import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UserConfig implements DatabaseEntity {
    public UserConfig() {

    }

    @SuppressWarnings("squid:S00107")
    public UserConfig(String image, long money, long level, long xp, long claim, long reputation, Language language, String id, Map<String, String> kvStore, Map<String, ArrayList<String>> listStore, int theme, boolean noDMs) {
        this.id = id;
        this.language = language;
        this.level = level;
        this.money = money;
        this.xp = xp;
        this.claim = claim;
        this.reputation = reputation;
        this.image = image;
        this.kvStore = kvStore;
        this.listStore = listStore;
        this.theme = theme;
        this.noDMs = noDMs;
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
    private boolean noDMs = false;
    private String image = "default";
    private Map<String, String> kvStore = new HashMap<>();
    private Map<String, ArrayList<String>> listStore = new HashMap<>();

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