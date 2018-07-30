package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.bot.util.UserUtil
import moe.kyokobot.moderation.ModerationCommand
import moe.kyokobot.moderation.ModerationIcons.BAN
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member

class BanCommand: ModerationCommand("ban", Permission.BAN_MEMBERS) {
    override fun execute(context: CommandContext) {
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
            val (number, purgeDays) = if (context.args.size > 1) {
                val arg = context.args[1]
                try {
                    Pair(2, Integer.parseUnsignedInt(arg))
                } catch (err: NumberFormatException) {
                    Pair(1, 0)
                }
            }
            else {
                Pair(1, 0)
            }

            val (reasonObj, reasonString) = if (context.args.size > number) {
                val arg = context.skipConcatArgs(number)
                Pair(arg, arg)
            } else {
                Pair(null, context.getTranslated("moderation.noreason"))
            }

            context.guild.controller.ban(member, purgeDays, reasonObj).queue({
                val translated = String.format(context.getTranslated("moderation.ban.output"), formattedName, reasonString, purgeDays)
                context.send("$BAN$translated")
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