package moe.kyokobot.music.commands

import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.music.MusicManager

class UnbindCommand(val musicManager: MusicManager): MusicCommand() {
    init {
        name = "unbind"
        checkChannel = true
        usage = ""
    }

    override fun preExecute(context: CommandContext) {
        val queue = musicManager.getQueue(context.guild)
        if (queue.boundChannel == null) {
            context.send("${CommandIcons.ERROR}${context.getTranslated("music.unbind.notbound")}")
            return
        }
        if (queue.binder != null && !context.member.canInteract(queue.binder)) {
            CommonErrors.noPermissionUser(context)
            return
        }
        super.preExecute(context)
    }

    override fun execute(context: CommandContext) {
        val queue = musicManager.getQueue(context.guild)
        val old = queue.boundChannel
        queue.binder = null
        queue.boundChannel = null
        context.send("${CommandIcons.SUCCESS}${context.getTranslated("music.unbind.success").format(old?.name)}")
    }
}