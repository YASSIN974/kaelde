package moe.kyokobot.bot.command.debug

import io.sentry.Sentry
import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.command.CommandType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class ShellCommand: Command() {
    init {
        this.name = "shell"
        this.type = CommandType.DEBUG
    }
    override fun execute(context: CommandContext?) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf(System.getenv("SHELL")!!, "-c", context!!.concatArgs.replace("\"", "\\\"")))
            val output = BufferedReader(InputStreamReader(process.inputStream))
            val err = BufferedReader(InputStreamReader(process.errorStream))
            val builder = StringBuilder("Response:```\n")
            process.waitFor(20, TimeUnit.SECONDS)
            if (output.ready()) {
                output.use {
                    builder.append(output.lines().toArray().joinToString(System.lineSeparator()))
                }
            }
            if (err.ready()) {
                err.use {
                    builder.append(err.lines().toArray().joinToString(System.lineSeparator()))
                }
            }
            process.destroyForcibly()
            var result = builder.toString()
            if (result.length > 1993)
                result = result.substring(0, 1993)
            context.send("```\n$result```")
        } catch (err: Throwable) {
            Sentry.capture(err)
            context?.send("${CommandIcons.ERROR} Error while running shell command: ${err.message}")
        }
    }
}