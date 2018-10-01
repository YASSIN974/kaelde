package moe.kyokobot.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.util.StringUtil
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import java.util.*

class MusicQueue(val manager: MusicManager, val guild: Guild) {
    var lastTrack: AudioTrack? = null
        get() = if (tracks.isEmpty()) null else tracks.removeFirst()
        private set

    var announcingChannel: TextChannel? = null
        get() = context?.channel
        private set
    var context: CommandContext? = null

    var repeating = false
    val tracks = LinkedList<AudioTrack>()

    fun clear() = tracks.clear()
    fun poll() = lastTrack
    fun shuffle() = tracks.shuffle()
    fun add(track: AudioTrack) {
        if (tracks.size < 250)
            tracks.add(track)
    }
    fun isEmpty() = tracks.isEmpty()
    fun remove(index: Int) {
        if (index >= tracks.size) return
        tracks.removeAt(index)
    }

    fun announce(track: AudioTrack) {
        val ch = announcingChannel // because announcingChannel is a mutable property, could've changed
        ch?.sendMessage(MusicIcons.PLAY + context?.getTranslated("music.nowplaying")?.format(
                track.info.title.replace("`", "\\`"),
                StringUtil.musicPrettyPeriod(track.duration)))?.queue()
    }
}