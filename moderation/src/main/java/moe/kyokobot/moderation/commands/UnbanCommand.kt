package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandCategory
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.bot.util.UserUtil
import net.dv8tion.jda.core.Permission

class UnbanCommand: Command() {
    init {
        name = "unban"
        description = "moderation.unban.description"
        usage = "moderation.unban.usage"
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
        val name = context.args[0]
        val ban = UserUtil.getBan(context.guild, name)
        if (ban == null) {
            val translated = String.format(context.getTranslated("moderation.unban.nobanfound"), name)
            context.send("${CommandIcons.ERROR}$translated")
            return
        }
        val formattedName = "${ban.user.name}#${ban.user.discriminator}"
        try {
            context.guild.controller.unban(ban.user).queue({
                val translated = String.format(context.getTranslated("moderation.unban.output"), formattedName, ban.reason)
                context.send("${CommandIcons.SUCCESS}$translated")
            }) { err ->
                Sentry.capture(err)
                val error = String.format(context.getTranslated("moderation.unban.error"), formattedName, err.message)
                context.send("${CommandIcons.ERROR}$error")
            }
        } catch (err: Throwable) {
            Sentry.capture(err)
            val error = String.format(context.getTranslated("moderation.unban.error"), formattedName, err.message)
            context.send("${CommandIcons.ERROR}$error")
        }
    }
}