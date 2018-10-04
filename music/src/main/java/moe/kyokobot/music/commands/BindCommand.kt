package moe.kyokobot.music.commands

import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.music.MusicManager

class BindCommand(val manager: MusicManager): MusicCommand() {
    init {
        name = "bind"
        checkChannel = true
    }

    override fun preExecute(context: CommandContext) {
        val queue = manager.getQueue(context.guild)
        if (queue.context != null && !context.member.canInteract(queue.context?.member)) {
            CommonErrors.noPermissionUser(context)
            return
        }
        super.preExecute(context)
    }

    override fun execute(context: CommandContext) {
        val queue = manager.getQueue(context.guild)
        queue.binder = context.member
        queue.boundChannel = if (context.hasArgs()) {
            val first = context.args[0]
            context.guild.textChannels.firstOrNull { it.id == first || first == "<#${it.id}>" || it.name.startsWith(first, true) } ?: context.channel
        }
        else {
            context.channel
        }
        context.send("${CommandIcons.SUCCESS}${context.getTranslated("music.bind.success").format(queue.boundChannel?.asMention)}")
    }
}