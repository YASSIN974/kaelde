package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.bot.util.UserUtil
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageHistory
import net.dv8tion.jda.core.utils.MiscUtil
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

class PruneCommand: Command() {
    init {
        name = "prune"
        description = "moderation.prune.description"
        usage = "moderation.prune.usage"
    }

    override fun execute(context: CommandContext) {
        if (!context.message.isFromType(ChannelType.TEXT)) {
            val translated = context.getTranslated("moderation.prune.cannotpruneprivate")
            context.send("${CommandIcons.ERROR}$translated")
            return
        }
        if (!context.selfMember.hasPermission(Permission.MESSAGE_MANAGE)) {
            CommonErrors.noPermissionBot(context, Permission.MESSAGE_MANAGE)
            return
        }
        if (!context.selfMember.hasPermission(Permission.MESSAGE_HISTORY)) {
            CommonErrors.noPermissionBot(context, Permission.MESSAGE_HISTORY)
            return
        }
        if (!context.member.hasPermission(Permission.MESSAGE_MANAGE) || !context.member.hasPermission(Permission.MESSAGE_HISTORY)) {
            CommonErrors.noPermissionUser(context)
            return
        }
        val (number, amountToClear) = if (context.hasArgs()) {
            try {
                val arg = context.args[0]
                val num = Integer.parseUnsignedInt(arg)
                if (num > 100) {
                    val translated = context.getTranslated("moderation.prune.cannotprunenumber")
                    context.send("${CommandIcons.ERROR}$translated")
                    return
                }
                Pair(1, num)
            } catch (err: NumberFormatException) {
                Pair(0, 15)
            }
        } else {
            Pair(-1, 15)
        }
        if (amountToClear == 0) {
            val translated = String.format(context.getTranslated("moderation.prune.output"), 0, 0)
            context.send("${CommandIcons.SUCCESS}$translated")
            return
        }
        var shouldAllowAll = true
        val mentioned: LongSet? = if (number != -1 && context.args.size > number) {
            val set = LongOpenHashSet()
            context.args
                    .asSequence()
                    .drop(number)
                    .map { str ->
                        val id = UserUtil.getMember(context.guild, str)?.user?.idLong
                        if (id != null) {
                            shouldAllowAll = false
                            id
                        } else -1
                    }
                    .forEach { set.add(it) }
            set
        } else {
            null
        }
        val timestamp = MiscUtil.getDiscordTimestamp(System.currentTimeMillis()).toString()
        MessageHistory.getHistoryBefore(context.channel, timestamp).limit(amountToClear).queue({ history ->
            val filtered = history.retrievedHistory
                    .stream()
                    .filter { msg ->
                        (shouldAllowAll || mentioned!!.contains(msg.author.idLong))
                                && ChronoUnit.WEEKS.between(msg.creationTime, OffsetDateTime.now()) < 2
                    }
                    .collect(Collectors.toList())
            if (filtered.isEmpty()) {
                val translated = String.format(context.getTranslated("moderation.prune.output"), 0, 0)
                context.send("${CommandIcons.SUCCESS}$translated")
                return@queue
            }
            if (filtered.size == 1) {
                filtered.first().delete().queue({
                    val translated = String.format(context.getTranslated("moderation.prune.output"), 1, 1)
                    context.send("${CommandIcons.SUCCESS}$translated")
                }) {
                    handleError(context, it)
                }
                return@queue
            }
            context.channel.deleteMessages(filtered).queue({
                handleSuccess(context, filtered)
            }) {
                handleError(context, it)
            }

        }) {
            handleError(context, it)
        }
    }
    private fun handleSuccess(context: CommandContext, filtered: List<Message>) {
        val unique: Map<String, List<String>> = filtered
                .stream()
                .map { it.author.id }
                .collect(Collectors.groupingBy { it })
        val translated = String.format(context.getTranslated("moderation.prune.output"), filtered.size, unique.keys.size)
        context.send("${CommandIcons.SUCCESS}$translated") {
            it.delete().queueAfter(2, TimeUnit.SECONDS, {

            }) {

            }
        }
    }
    private fun handleError(context: CommandContext, err: Throwable) {
        val translated = String.format(context.getTranslated("moderation.prune.error"), err.message)
        Sentry.capture(err)
        context.send("${CommandIcons.ERROR}$translated")
    }
}