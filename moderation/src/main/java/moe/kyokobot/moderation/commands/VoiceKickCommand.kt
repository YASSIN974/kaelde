package moe.kyokobot.moderation.commands

import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.util.UserUtil
import moe.kyokobot.moderation.ModerationCommand
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.VoiceChannel

class VoiceKickCommand : ModerationCommand("voicekick", Permission.MANAGE_CHANNEL, hasArgs = true) {
    init {
        aliases = arrayOf("vckick", "vkick", "getthefuckoffmychannel")
    }

    override fun execute(context: CommandContext) {
        val members = context.args.mapNotNull { UserUtil.getMember(context.guild, it) }

        context.guild.controller.createVoiceChannel("\uD83D\uDEAE bye kidz").queue { chan ->
            if (members.isEmpty()) {
                context.error(context.getTranslated("moderation.nomembers"))
                return@queue
            }

            members.forEach { member ->
                if (member.voiceState.inVoiceChannel())
                    context.guild.controller.moveVoiceMember(member, chan as VoiceChannel).complete()
            }

            chan.delete().queue()
            context.success(context.transFormat("moderation.voicekick.kicked", members.size))
        }
    }
}
