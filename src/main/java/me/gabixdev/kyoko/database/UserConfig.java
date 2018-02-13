package me.gabixdev.kyoko.database;

import me.gabixdev.kyoko.i18n.Language;

import javax.persistence.*;

@Table(name = "users")
public class UserConfig {

    public UserConfig() {
    }

    public UserConfig(long userId, String language, int level, int money, int xp) {
        this.userId = userId;
        this.strlanguage = language;
        this.language = Language.valueOf(strlanguage);
        this.level = level;
        this.money = money;
        this.xp = xp;
    }

    @Id
    @GeneratedValue
    public long id;

    private long userId;
    @Transient
    private Language language;
    private String strlanguage;
    private int level;
    private int money;
    private int xp;

    @Column(name = "userid")
    public long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "lang")
    public String getStrLanguage() {
        return strlanguage;
    }

    public void setStrLanguage(String language) {
        this.language = Language.valueOf(language);
        this.strlanguage = language;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    @Transient
    public Language getLanguage() {
        if (language == null) {
            if (strlanguage == null)
                strlanguage = Language.ENGLISH.name();
            language = Language.valueOf(strlanguage);
            return language;
        } else
            return language;
    }

    @Transient
    public void setLanguage(Language language) {
        this.language = language;
        this.strlanguage = language.name();
    }

    public String toString() {
        return id + " " + userId + " " + language + " " + level + " " + money + " " + xp;
    }
}