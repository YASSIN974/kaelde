package moe.kyokobot.misccommands.commands.basic;

import com.sun.management.OperatingSystemMXBean;
import de.vandermeer.asciitable.AsciiTable;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.StringUtil;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class StatsCommand extends Command {
    private final RuntimeMXBean rb;
    private final OperatingSystemMXBean osb;
    private final CommandManager commandManager;

    public StatsCommand(CommandManager commandManager) {
        name = "stats";
        aliases = new String[] {"botinfo", "about"};

        this.commandManager = commandManager;
        rb = ManagementFactory.getRuntimeMXBean();
        osb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    }

    @Override
    public void execute(@NotNull CommandContext context) {
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow(null, context.getTranslated("about.title"));
        at.addRule();
        at.addRow("Bot version", "v" + Constants.VERSION);
        at.addRow("Library", "JDA " + JDAInfo.VERSION);
        at.addRow("Author", "gabixdev#0001");
        at.addRow("Shard", context.getEvent().getJDA().getShardInfo() + " (" + context.getEvent().getJDA().getGuilds().size() + " guilds)");
        at.addRow("Uptime", StringUtil.prettyPeriod(rb.getUptime()));
        at.addRow("Commands executed", commandManager.getRuns());
        at.addRow("Audio connections", context.getEvent().getJDA().getAudioManagers().stream().filter(AudioManager::isConnected).count());
        at.addRow("CPU usage", Math.floor(osb.getProcessCpuLoad() * 100) + "%");
        at.addRule();

        context.send("```markdown\n" + at.render(50) + "\n```");
    }
}
