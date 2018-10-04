package moe.kyokobot.music.commands

import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.music.MusicManager
import net.dv8tion.jda.core.Permission

class JoinCommand(val musicManager: MusicManager): MusicCommand() {
    init {
        name = "join"
    }

    override fun execute(context: CommandContext) {
        try {
            val state = context.member.voiceState
            val channel = if (context.hasArgs()) {
                context.guild.voiceChannels.firstOrNull { it.name.startsWith(context.concatArgs, true) }
            } else {
                state.channel
            }
            if (channel == null) {
                context.send(CommandIcons.ERROR + context.getTranslated("music.join.nochannelfound"))
                return
            }
            val selfState = context.selfMember.voiceState
            if (selfState.inVoiceChannel()) {
                if (channel.idLong == selfState.channel.idLong) {
                    context.send(CommandIcons.ERROR + context.getTranslated("music.join.insamechannel").format(channel.name))
                    return
                }
                if (context.member.canInteract(context.selfMember)) {
                    if (context.member.hasPermission(Permission.VOICE_MOVE_OTHERS) || context.member.hasPermission(Permission.ADMINISTRATOR)) {
                        context.guild.controller.moveVoiceMember(context.selfMember, channel).queue({
                            context.send(CommandIcons.SUCCESS + context.getTranslated("music.join.joinedchannel").format(channel.name))
                        }) {
                            CommonErrors.exception(context, it)
                        }
                        return
                    }
                    val manager = context.guild.audioManager
                    if (manager.isConnected)
                        manager.closeAudioConnection()
                    manager.openAudioConnection(channel)
                    val queue = musicManager.getQueue(context.guild)
                    val ch = (queue.boundChannel ?: queue.announcingChannel) ?: context.channel
                    ch.sendMessage(CommandIcons.SUCCESS + context.getTranslated("music.join.joinedchannel").format(channel.name)).queue()
                    return
                }
                context.send(CommandIcons.ERROR + context.getTranslated("music.join.cannotinteract"))
                return
            }
            context.guild.audioManager.openAudioConnection(channel)
            val queue = musicManager.getQueue(context.guild)
            val ch = (queue.boundChannel ?: queue.announcingChannel) ?: context.channel
            ch.sendMessage(CommandIcons.SUCCESS + context.getTranslated("music.join.joinedchannel").format(channel.name)).queue()
        } catch (err: Throwable) {
            Command.logger.error("Error when attempting to join a channel!", err)
            CommonErrors.exception(context, err)
        }
    }
}