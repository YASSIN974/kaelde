package moe.kyokobot.bot.entity

import com.google.gson.annotations.SerializedName
import moe.kyokobot.bot.i18n.Language
import moe.kyokobot.bot.util.GsonUtil

import java.beans.Transient
import java.util.ArrayList

class GuildConfig(guildId: String, language: Language, prefixes: ArrayList<String>) : DatabaseEntity {

    @SerializedName("guild-id")
    var guildId = ""
    var language = Language.ENGLISH
    var prefixes = ArrayList<String>()
    var musicConfig = MusicConfig()

    init {
        this.guildId = guildId
        this.language = language
        this.prefixes = prefixes
    }

    @Transient
    override fun getTableName(): String {
        return "guilds"
    }

    override fun toString(): String {
        return GsonUtil.toJSON(this)
    }

    inner class MusicConfig {
        @SerializedName("dj-role")
        var djRole = ""
    }
}