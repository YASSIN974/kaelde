package moe.kyokobot.misccommands.commands.images

import io.sentry.Sentry
import moe.kyokobot.bot.Constants
import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandCategory
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.util.CommonErrors
import moe.kyokobot.bot.util.GsonUtil
import moe.kyokobot.bot.util.NetworkUtil
import net.dv8tion.jda.core.entities.Message

class CoffeeCommand: Command() {
    init {
        name = "coffee"
        description = "coffee.description"
        category = CommandCategory.IMAGES
    }
    override fun execute(context: CommandContext?) {
        // this command is written with null safety -- if a variable is null, the command won't do anything
        // better than possibly throwing NPE's everywhere though
        context?.send(CommandIcons.working + context.getTranslated("generic.loading")) {
            message: Message? ->
            try {
                val data = String(NetworkUtil.download("https://coffee.alexflipnote.xyz/random.json"))
                val response = GsonUtil.fromJSON(data, CoffeeResponse::class.java)
                if (response.file == null || response.file.isEmpty()) {
                    message?.editMessage(CommandIcons.error + context.getTranslated("api.coffee.error"))?.queue()
                } else {
                    val eb = context.normalEmbed ?: return@send // if normal embed is somehow null, return out of lambda
                    eb.addField(context.getTranslated("coffee.title"), Constants.POWERED_BY_ALEX, false)
                    eb.setImage(response.file)
                    message?.editMessage(eb.build())?.override(true)?.queue()
                }
            } catch (err: Throwable) {
                logger.error("Error during image send!", err)
                Sentry.capture(err)
                CommonErrors.editException(context, err, message)
            }
        }
    }
    private data class CoffeeResponse(val file: String?)
}