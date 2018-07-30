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
        val (number, amountToClear, containMode) = if (context.hasArgs()) {
            val arg = context.args[0]
            try {
                val num = Integer.parseUnsignedInt(arg)
                if (num > 100) {
                    val translated = context.getTranslated("moderation.prune.cannotprunenumber")
                    context.send("${CommandIcons.ERROR}$translated")
                    return
                }
                Triple(1, num, false)
            } catch (err: NumberFormatException) {
                Triple(0, 15, arg.equals("contains", true))
            }
        } else {
            Triple(-1, 15, false)
        }
        if (amountToClear == 0) {
            val translated = String.format(context.getTranslated("moderation.prune.output"), 0, 0)
            context.send("${CommandIcons.SUCCESS}$translated")
            return
        }
        var shouldAllowAll = true
        var addedBots = false
        val mentioned: LongSet? = if (!containMode && number != -1 && context.args.size > number) {
            val set = LongOpenHashSet()
            context.args
                    .asSequence()
                    .drop(number)
                    .map { str ->
                        if (str.equals("bots", true) && !addedBots) {
                            context.guild.members
                                    .filter { it.user.isBot }
                                    .forEach { set.add(it.user.idLong) }
                            addedBots = true
                            shouldAllowAll = false

                        }
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
        val trueAmount = if (shouldAllowAll) amountToClear else 99
        val timestamp = MiscUtil.getDiscordTimestamp(System.currentTimeMillis()).toString()
        MessageHistory.getHistoryBefore(context.channel, timestamp).limit(trueAmount + 1).queue({ history ->
            val content = if (containMode) context.skipConcatArgs(1) else null
            val filteredStream = history.retrievedHistory
                    .stream()
                    .skip(1)
                    .filter { msg ->
                        ((containMode && msg.contentRaw.contains(content!!))
                        || (shouldAllowAll || mentioned!!.contains(msg.author.idLong)))
                            && ChronoUnit.WEEKS.between(msg.creationTime, OffsetDateTime.now()) < 2
                    }
            if (!shouldAllowAll)
                filteredStream.limit(trueAmount.toLong())
            val filtered = filteredStream
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
        context.send("${CommandIcons.SUCCESS}$translated") { msg ->
            val two = listOf(context.message, msg)
            context.channel.deleteMessages(two).queueAfter(5, TimeUnit.SECONDS, null) {
                handleError(context, it, true)
            }
        }
    }
    private fun handleError(context: CommandContext, err: Throwable, self: Boolean = false) {
        val str = if (!self) "moderation.prune.error" else "moderation.prune.errorcleaningself"
        val translated = String.format(context.getTranslated(str), err.message)
        Sentry.capture(err)
        context.send("${CommandIcons.ERROR}$translated")
    }
}