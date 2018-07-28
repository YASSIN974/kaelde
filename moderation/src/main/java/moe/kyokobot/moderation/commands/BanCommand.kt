package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandCategory
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.bot.util.UserUtil
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member

class BanCommand: Command() {
    init {
        name = "ban"
        description = "moderation.ban.description"
        usage = "moderation.ban.usage"
        category = CommandCategory.MODERATION
    }

    override fun execute(context: CommandContext) {
        if (!context.selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            CommonErrors.noPermissionBot(context, Permission.BAN_MEMBERS)
            return
        }
        if (!context.member.hasPermission(Permission.BAN_MEMBERS)) {
            CommonErrors.noPermissionUser(context)
            return
        }
        if (!context.hasArgs()) {
            CommonErrors.usage(context)
            return
        }
        val memberName = context.args[0]
        val member: Member? = UserUtil.getMember(context.guild, memberName)
        if (member == null) {
            CommonErrors.noUserFound(context, memberName)
            return
        }
        val formattedName = "${member.user.name}#${member.user.discriminator}"
        if (!context.member.canInteract(member)) {
            val name = String.format(context.getTranslated("moderation.ban.cannotban"), formattedName)
            context.send("${CommandIcons.ERROR}$name")
            return
        }
        try {
            val (reasonObj, reasonString) = if (context.args.size > 2) {
                val arg = context.skipConcatArgs(2)
                Pair(arg, arg)
            } else {
                Pair(null, "No reason provided.")
            }
            val purgeDays: Int = if (context.args.size > 1) {
                val arg = context.args[1]
                try {
                    Integer.parseUnsignedInt(arg)
                } catch (err: NumberFormatException) {
                    CommonErrors.notANumber(context, arg)
                    return
                }
            }
            else {
                0
            }
            context.guild.controller.ban(member, purgeDays, reasonObj).queue({
                val translated = String.format(context.getTranslated("moderation.ban.output"), formattedName, reasonString, purgeDays)
                context.send("${CommandIcons.SUCCESS}$translated")
            }) { err ->
                Sentry.capture(err)
                val error = String.format(context.getTranslated("moderation.ban.error"), formattedName, err.message)
                context.send("${CommandIcons.ERROR}$error")
            }
        } catch (err: Throwable) {
            Sentry.capture(err)
            val error = String.format(context.getTranslated("moderation.ban.error"), formattedName, err.message)
            context.send("${CommandIcons.ERROR}$error")
        }
    }
}