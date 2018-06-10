package moe.kyokobot.bot.entity

import moe.kyokobot.bot.i18n.Language
import moe.kyokobot.bot.util.GsonUtil

import java.beans.Transient
import java.util.ArrayList

class UserConfig(image: String, money: Long, level: Long, xp: Long, claim: Long, reputation: Long, language: Language, id: String, tags: ArrayList<String>) : DatabaseEntity {

    var id = ""
    var language = Language.DEFAULT
    var level = 1L
    var money = 0L
    var xp = 0L
    var claim = 0L
    var reputation = 0L
    var image = "default"
    var tags = ArrayList<String>()

    init {
        this.id = id
        this.language = language
        this.level = level
        this.money = money
        this.xp = xp
        this.claim = claim
        this.reputation = reputation
        this.image = image
        this.tags = tags
    }

    @Transient
    override fun getTableName(): String {
        return "users"
    }

    override fun toString(): String {
        return GsonUtil.toJSON(this)
    }
}