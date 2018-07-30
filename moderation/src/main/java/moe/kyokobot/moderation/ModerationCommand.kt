package moe.kyokobot.moderation

import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandCategory
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.util.CommonErrors
import net.dv8tion.jda.core.Permission

open class ModerationCommand(name: String, private vararg val permissions: Permission = emptyArray(), private val hasArgs: Boolean = true): Command() {
    init {
        super.name = name
        description = "moderation.$name.description"
        usage = "moderation.$name.usage"
        category = CommandCategory.MODERATION
    }

    override fun preExecute(context: CommandContext) {
        if (hasArgs && !context.hasArgs()) {
            CommonErrors.usage(context)
            return
        }
        if (!context.member.hasPermission(Permission.ADMINISTRATOR)) {
            if (context.member.hasPermission(*permissions)) {
                CommonErrors.noPermissionUser(context)
                return
            }
        }
        if (context.selfMember.hasPermission(Permission.ADMINISTRATOR)) {
            super.preExecute(context)
            return
        }
        /* -- An iterator is used over varargs here so I can get the specific permission the bot lacks. -- */
        var failed = false
        permissions.forEach { perm ->
            if (!context.selfMember.hasPermission(perm)) {
                CommonErrors.noPermissionBot(context, perm)
                failed = true
                return
            }
        }
        if (failed)
            return
        super.preExecute(context)
    }

    fun getTranslated(context: CommandContext, key: String, vararg params: Any? = emptyArray()): String? =
            context.getTranslated("moderation.$name.$key")?.format(*params)
}