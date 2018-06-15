package moe.kyokobot.bot.entity;

import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;

@Getter
@Setter
public class UserConfig implements DatabaseEntity {
    public UserConfig() {

    }

    public UserConfig(String image, long money, long level, long xp, long claim, long reputation, Language language, String id, ArrayList<String> tags) {
        this.id = id;
        this.language = language;
        this.level = level;
        this.money = money;
        this.xp = xp;
        this.claim = claim;
        this.reputation = reputation;
        this.image = image;
        this.tags = tags;
    }

    private String id = "";
    private Language language = Language.DEFAULT;
    private long level = 1L;
    private long money = 0L;
    private long xp = 0L;
    private long claim = 0L;
    private long reputation = 0L;
    private String image = "default";
    private ArrayList<String> tags = new ArrayList<>();

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