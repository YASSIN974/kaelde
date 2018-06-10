package moe.kyokobot.bot.command

import moe.kyokobot.bot.Settings
import moe.kyokobot.bot.i18n.I18n
import moe.kyokobot.bot.i18n.Language
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.awt.Color
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class CommandContext(val settings: Settings, val i18n: I18n, val command: Command, val event: MessageReceivedEvent, val prefix: String, val label: String, concatArgs: String, val args: Array<String>) {
    val language: Language
    val concatArgs: String

    val sender: User
        get() = event.author

    val member: Member
        get() = event.member

    val channel: TextChannel
        get() = event.textChannel

    val guild: Guild
        get() = event.guild

    val message: Message
        get() = event.message

    val normalEmbed: EmbedBuilder
        get() {
            val eb = EmbedBuilder()
            eb.setColor(normalColor)
            //eb.setFooter(  "kyokobot v" + Constants.VERSION + " | created by gabixdev & contributors", null);
            return eb
        }

    val normalColor: Color
        get() {
            var c = settings.bot.normalColor

            if (event.member != null) {
                if (event.member.color != null) {
                    c = event.member.color
                }
            }

            return c
        }

    val errorEmbed: EmbedBuilder
        get() {
            val eb = EmbedBuilder()
            eb.setColor(settings.bot.errorColor)
            //eb.setFooter(settings.bot.botName + " v" + Constants.VERSION + " | created by gabixdev & contributors", null)
            return eb
        }

    init {
        if (event.channelType.isGuild)
            this.language = i18n.getLanguage(event.member)
        else
            this.language = i18n.getLanguage(event.author)
        this.concatArgs = concatArgs.trim { it <= ' ' }
    }

    fun hasArgs(): Boolean {
        return !concatArgs.isEmpty()
    }

    fun skipConcatArgs(n: Int): String {
        return Arrays.stream(args).skip(n.toLong()).collect(Collectors.joining(" "))
    }

    @JvmOverloads
    fun send(message: CharSequence, callback: Consumer<Message>? = null) {
        if (checkSensitive(message.toString())) {
            event.channel.sendMessage(CommandIcons.error + getTranslated("generic.sensitive")).queue(callback)
        } else {
            event.channel.sendMessage(message).queue(callback)
        }
    }

    @JvmOverloads
    fun send(message: MessageEmbed, callback: Consumer<Message>? = null) {
        event.channel.sendMessage(message).queue(callback)
    }

    fun getTranslated(key: String): String {
        return i18n.get(language, key)
    }

    fun checkSensitive(input: String): Boolean {
        return if (input.contains(settings.connection.token)) true else settings.apiKeys.values.stream().anyMatch({ input.contains(it) })
    }
}
