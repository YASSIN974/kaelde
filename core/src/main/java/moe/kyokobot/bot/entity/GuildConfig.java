package moe.kyokobot.bot.entity;


import moe.kyokobot.bot.i18n.Language;

public class GuildConfig {

    public GuildConfig() {
    }

    public GuildConfig(Long guildId, Language language, String prefixes) {
        this.guildId = guildId;
        this.language = language;
        this.prefixes = prefixes;
    }

    public Long guildId;
    public Language language;
    public String prefixes;


    public String toString() {
        return guildId + " " + language + " " + prefixes;
    }
}