package moe.kyokobot.misccommands

import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import moe.kyokobot.bot.command.Command
import moe.kyokobot.bot.manager.CommandManager
import moe.kyokobot.bot.module.KyokoModule
import moe.kyokobot.misccommands.commands.*
import java.util.*

class Module : KyokoModule {
    @Inject
    private val commandManager: CommandManager? = null
    @Inject
    private val eventBus: EventBus? = null
    private val commands: ArrayList<Command> = ArrayList()

    override fun startUp() {
        commands.add(HelpCommand(commandManager))

        commands.add(CoffeeCommand())

        commands.add(PingCommand(commandManager))
        commands.add(SayCommand(commandManager))
        commands.add(AvatarCommand(commandManager))
        commands.add(UserInfoCommand(commandManager))
        commands.add(ServerInfoCommand(commandManager))

        commands.add(WhyCommand())
        commands.add(OwOifyCommand())
        commands.add(SimpleTextCommand("lenny", "( ͡° ͜ʖ ͡°)"))
        commands.add(SimpleTextCommand("shrug", "¯\\_(ツ)_/¯"))
        commands.add(RandomTextCommand("tableflip", arrayOf(" (╯°□°）╯︵ ┻━┻", "(┛◉Д◉)┛彡┻━┻", "(ﾉ≧∇≦)ﾉ ﾐ ┸━┸", "(ノಠ益ಠ)ノ彡┻━┻", "(╯ರ ~ ರ）╯︵ ┻━┻", "(┛ಸ_ಸ)┛彡┻━┻", "(ﾉ´･ω･)ﾉ ﾐ ┸━┸", "(ノಥ,_｣ಥ)ノ彡┻━┻", "(┛✧Д✧))┛彡┻━┻")))
        commands.add(SnipeCommand(eventBus))

        commands.forEach({ commandManager!!.registerCommand(it) })
    }

    override fun shutDown() {
        commands.forEach({ commandManager!!.unregisterCommand(it) })
    }
}
