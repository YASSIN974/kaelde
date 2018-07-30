package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.UserUtil
import moe.kyokobot.moderation.ModerationCommand
import net.dv8tion.jda.core.Permission

class UnbanCommand: ModerationCommand("unban", Permission.BAN_MEMBERS) {
    init {
        aliases = arrayOf("pardon")
    }
    override fun execute(context: CommandContext) {
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
                val translated = getTranslated(context, "output", formattedName, ban.reason ?: getTranslated(context, "noreason") ?: "No reason.")
                context.send("${CommandIcons.SUCCESS}$translated")
            }) { err ->
                Sentry.capture(err)
                val error = getTranslated(context, "error", formattedName, err.message ?: "No message.")
                context.send("${CommandIcons.ERROR}$error")
            }
        } catch (err: Throwable) {
            Sentry.capture(err)
            val error = getTranslated(context, "error", formattedName, err.message ?: "No message.")
            context.send("${CommandIcons.ERROR}$error")
        }
    }
}