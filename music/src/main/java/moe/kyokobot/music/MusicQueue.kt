package moe.kyokobot.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.util.StringUtil
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import java.util.*
import java.util.function.Predicate

class MusicQueue(val manager: MusicManager, val guild: Guild) {
    var lastTrack: AudioTrackWrapper? = null
        get() = if (tracks.isEmpty()) null else tracks.removeFirst()
        private set

    var binder: Member? = null
    var boundChannel: TextChannel? = null
    var announcingChannel: TextChannel? = null
        get() = context?.channel
    var context: CommandContext? = null

    var repeating = false
    val tracks = LinkedList<AudioTrackWrapper>()

    fun clear() = tracks.clear()
    fun poll() = lastTrack
    fun shuffle() = tracks.shuffle()
    fun add(track: AudioTrackWrapper) {
        if (tracks.size < 250)
            tracks.add(track)
    }
    fun isEmpty() = tracks.isEmpty()
    fun remove(index: Int) {
        if (index >= tracks.size) return
        tracks.removeAt(index)
    }
    fun removeUser(user: String) {
        tracks.removeIf { track -> track.user == user }
    }

    fun removeDuplicates() {
        for(i in 1 until tracks.size) {
           if(tracks.get(i).audioTrack.equals(tracks.get(i - 1).audioTrack)) {
               tracks.removeAt(i);
           }
        }
    }

    fun announce(track: AudioTrackWrapper) {
        val channel = if (boundChannel != null) boundChannel else announcingChannel
        channel?.sendMessage(MusicIcons.PLAY + context?.getTranslated("music.nowplaying")?.format(
                track.audioTrack.info.title.replace("`", "\\`"),
                StringUtil.musicPrettyPeriod(track.audioTrack.duration)))?.queue()
    }
}