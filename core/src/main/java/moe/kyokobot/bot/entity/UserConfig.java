package moe.kyokobot.bot.entity;

import moe.kyokobot.bot.i18n.Language;

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
    public Long userId;
    public Language language;
    public int level;
    public Integer money;
    public Integer xp;
    public Long claim;
    public Integer reputation = 0;
    public String image = "default";

    public String toString() {
        return userId + " " + language + " " + level + " " + money + " " + xp + " " + claim;
    }
}