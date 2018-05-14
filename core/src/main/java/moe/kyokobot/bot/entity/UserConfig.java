package moe.kyokobot.bot.entity;

import moe.kyokobot.bot.i18n.Language;
import moe.kyokobot.bot.util.GsonUtil;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

public class UserConfig implements DatabaseEntity {
    public UserConfig(String image, long money, long level, long xp, long claim, long reputation, Language language, long id, ArrayList<String> tags) {
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

    public long id = 0;
    public Language language = Language.DEFAULT;
    public long level = 1L;
    public long money = 0L;
    public long xp = 0L;
    public long claim = 0L;
    public long reputation = 0L;
    public String image = "default";
    public ArrayList<String> tags = new ArrayList<>();

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