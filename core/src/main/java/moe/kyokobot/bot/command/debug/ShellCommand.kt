package moe.kyokobot.bot.command.debug

import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.command.CommandContext
import moe.kyokobot.bot.command.CommandIcons
import moe.kyokobot.bot.command.CommandType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit

class ShellCommand: Command() {
    init {
        this.name = "shell"
        this.type = CommandType.DEBUG
    }
    override fun execute(context: CommandContext) {
        try {
            val process = ProcessBuilder(Arrays.asList(System.getenv("SHELL")!!, "-c", context.concatArgs.replace("\"", "\\\""))).redirectErrorStream(true).start()
            val output = BufferedReader(InputStreamReader(process.inputStream))
            val builder = StringBuilder()
            process.waitFor(10, TimeUnit.MINUTES)
            if (output.ready()) {
                output.use {
                    builder.append(output.lines().toArray().joinToString(System.lineSeparator()))
                }
            }
            process.destroyForcibly()
            var result = builder.toString()
            if (result.length > 1993)
                result = result.substring(0, 1993)
            context.sendChecked("```\n$result```", null)

        } catch (err: Throwable) {
            context.send("${CommandIcons.ERROR} Error while running shell command: ${err.message}")
        }
    }
}