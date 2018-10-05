package moe.kyokobot.music.commands

import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.music.MusicManager
import java.util.concurrent.TimeUnit

class SeekCommand(val musicManager: MusicManager): MusicCommand() {
    init {
        name = "seek"
        checkChannel = true
        aliases = arrayOf("goto", "skipto")
    }

    override fun preExecute(context: CommandContext) {
        if (!context.hasArgs()) {
            CommonErrors.usage(context)
            return
        }
        val queue = musicManager.getQueue(context.guild)
        if (queue.context != null && !context.member.canInteract(queue.context?.member)) {
            CommonErrors.noPermissionUser(context)
            return
        }
        val playing = musicManager.getMusicPlayer(context.guild).playingTrack
        if (playing == null) {
            context.error(context.getTranslated("music.nothingplaying"))
            return
        }
        if (!playing.isSeekable || playing.duration == Long.MAX_VALUE) {
            context.error(context.getTranslated("music.seek.unseekable"))
            return
        }
        super.preExecute(context)
    }

    override fun execute(context: CommandContext) {
        val match = HMS.matchEntire(context.args[0])
        if (match == null) {
            CommonErrors.usage(context)
            return
        }
        val groups = match.groups
        val hours = groups[1]?.value?.replaceFirst("h", "")?.toLong() ?: 0
        val minutes = groups[2]?.value?.replaceFirst("m", "")?.toLong() ?: 0
        val seconds = groups[3]?.value?.replaceFirst("s", "")?.toLong() ?: 0
        val position = (seconds * 1000) + (minutes * 60000) + (hours * 3600000)
        val player = musicManager.getMusicPlayer(context.guild)
        if (position < 0 || position > player.playingTrack.duration) {
            context.error(context.getTranslated("music.seek.outofbounds"))
            return
        }
        player.seek(position)
        val queue = musicManager.getQueue(context.guild)
        val channel = (queue.boundChannel ?: queue.announcingChannel) ?: context.channel
        channel.sendMessage("${CommandIcons.SUCCESS}${context.transFormat("music.seek.success", formatHms(position))}").queue()
    }

    fun formatHms(timeMs: Long): String = when {
        timeMs >= 3600000 -> String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeMs) % 60,
                TimeUnit.MILLISECONDS.toMinutes(timeMs) % 60,
                TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60)
        timeMs >= 60000 -> String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMs) % 60,
                TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60)
        timeMs >= 1000 -> String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60)
        else -> "${timeMs}ms"
    }

    companion object {
        private val HMS = Regex("^(\\d{1,2}h)?([0-5]?\\dm)?([0-5]?\\ds)$")
    }
}