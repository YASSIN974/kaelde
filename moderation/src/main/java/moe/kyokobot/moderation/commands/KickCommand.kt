package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.bot.util.UserUtil
import moe.kyokobot.moderation.ModerationCommand
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member

class KickCommand: ModerationCommand("kick", Permission.KICK_MEMBERS) {
    override fun execute(context: CommandContext) {
        val memberName = context.args[0]
        val member: Member? = UserUtil.getMember(context.guild, memberName)
        if (member == null) {
            CommonErrors.noUserFound(context, memberName)
            return
        }
        val formattedName = "${member.user.name}#${member.user.discriminator}"
        if (!context.member.canInteract(member)) {
            val name = String.format(context.getTranslated("moderation.kick.cannotkick"), formattedName)
            context.send("${CommandIcons.ERROR}$name")
            return
        }
        try {
            val (reasonObj, reasonString) = if (context.args.size > 1) {
                val arg = context.skipConcatArgs(1)
                Pair(arg, arg)
            } else {
                Pair(null, "No reason provided.")
            }
            context.guild.controller.kick(member, reasonObj).queue({
                val translated = String.format(context.getTranslated("moderation.kick.output"), formattedName, reasonString)
                context.send("${CommandIcons.SUCCESS}$translated")
            }) { err ->
                Sentry.capture(err)
                val error = String.format(context.getTranslated("moderation.kick.error"), formattedName, err.message)
                context.send("${CommandIcons.ERROR}$error")
            }
        } catch (err: Throwable) {
            Sentry.capture(err)
            val error = String.format(context.getTranslated("moderation.kick.error"), formattedName, err.message)
            context.send("${CommandIcons.ERROR}$error")
        }
    }
}