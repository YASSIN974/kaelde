package moe.kyokobot.moderation.commands

import io.sentry.Sentry
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.UserUtil
import moe.kyokobot.moderation.ModerationCommand
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageHistory
import net.dv8tion.jda.core.utils.MiscUtil
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

class PruneCommand: ModerationCommand("prune", Permission.MESSAGE_HISTORY, Permission.MESSAGE_MANAGE, hasArgs = false) {
    override fun execute(context: CommandContext) {
        val args = context.args
        val size = args.size
        var (number, amountToClear, containMode) = Triple(0, 15, false)
        if (!args.isEmpty()) {
            try {
                val num = Integer.parseUnsignedInt(args[0])
                if (num > 100) {
                    val translated = getTranslated(context, "cannotprunenumber")
                    context.send("${CommandIcons.ERROR}$translated")
                    return
                }
                number = 1
                amountToClear = num
            }
            catch (ignored: NumberFormatException) {
                // this is already handled by the default values
            }
            finally {
                if (size > number) {
                    val contains = args[number]
                    if (contains.equals("contains", true) || contains.equals("-contains", true)) {
                        if (size > number + 1) {
                            containMode = true
                        } else {
                            val translated = getTranslated(context, "containserror")
                            context.send("${CommandIcons.ERROR}$translated")
                            return
                        }
                    }
                }
            }
        } else {
            //number = -1
            displayUsage(context)
            return
        }
        if (amountToClear == 0) {
            handleSuccess(context, null, 0)
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
                        if (str.equals("bots", true) || str.equals("-bots", true) && !addedBots) {
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
        val trueAmount = when {
            containMode -> 99
            shouldAllowAll -> amountToClear
            else -> 99
        }

        context.message.delete().queue {
            val timestamp = MiscUtil.getDiscordTimestamp(System.currentTimeMillis()).toString()
            MessageHistory.getHistoryBefore(context.channel, timestamp).limit(trueAmount + 1).queue({ history ->
                val content = if (containMode) context.skipConcatArgs(number + 1) else null
                var filteredStream = history.retrievedHistory
                        .stream()
                        .skip(1)
                        .filter { msg ->
                            val check = if (containMode) {
                                msg.contentRaw.contains(content!!)
                            } else {
                                shouldAllowAll || mentioned!!.contains(msg.author.idLong)
                            }
                            check && (ChronoUnit.WEEKS.between(msg.creationTime, OffsetDateTime.now()) < 2)
                        }
                if (!shouldAllowAll)
                    filteredStream = filteredStream.limit(trueAmount.toLong())
                val filtered = filteredStream
                        .collect(Collectors.toList())
                if (filtered.isEmpty()) {
                    handleSuccess(context, filtered, 0)
                    return@queue
                }
                if (filtered.size == 1) {
                    filtered.first().delete().queue({
                        handleSuccess(context, filtered, 1)
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
    }

    private fun displayUsage(context: CommandContext) {
        val eb = context.normalEmbed
        eb.setTitle(context.getTranslated("generic.usage"))
        val sb = StringBuilder()
        sb.append("`${context.prefix}prune 10` - ${context.getTranslated("moderation.prune.usage.messages")}\n")
        sb.append("`${context.prefix}prune bots` - ${context.getTranslated("moderation.prune.usage.bots1")}\n")
        sb.append("`${context.prefix}prune 50 bots` - ${context.getTranslated("moderation.prune.usage.bots2")}\n")
        sb.append("`${context.prefix}prune contains discord.gg` - ${context.getTranslated("moderation.prune.usage.contains1")}\n")
        sb.append("`${context.prefix}prune 30 contains please join` - ${context.getTranslated("moderation.prune.usage.contains2")}\n")
        sb.append("`${context.prefix}prune 25 ${context.sender.asMention}` - ${context.getTranslated("moderation.prune.usage.member")}")
        eb.setDescription(sb.toString())
        context.send(eb.build())

    }

    private fun handleSuccess(context: CommandContext, filtered: List<Message>?, placeholder: Int? = null) {
        val (filter, unique) = if (placeholder != null) {
            Pair(placeholder, placeholder)
        } else {
            val unique: Map<String, List<String>> = filtered!!
                    .stream()
                    .map { it.author.id }
                    .collect(Collectors.groupingBy { it })
            Pair(filtered.size, unique.keys.size)
        }
        val translated = getTranslated(context, "output", filter, unique)
        context.send("${CommandIcons.SUCCESS}$translated") { msg ->
            val two = listOf(context.message, msg)
            context.channel.deleteMessages(two).queueAfter(5, TimeUnit.SECONDS, null) {
                handleError(context, it, true)
            }
        }
    }

    private fun handleError(context: CommandContext, err: Throwable, self: Boolean = false) {
        val str = if (!self) "error" else "errorcleaningself"
        val translated = getTranslated(context, str, err.message ?: "No message.")
        Sentry.capture(err)
        context.send("${CommandIcons.ERROR}$translated")
    }
}