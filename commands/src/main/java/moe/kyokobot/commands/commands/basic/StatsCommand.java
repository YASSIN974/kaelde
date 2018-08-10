package moe.kyokobot.commands.commands.basic;

import com.sun.management.OperatingSystemMXBean;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.manager.CommandManager;
import moe.kyokobot.bot.util.StringUtil;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class StatsCommand extends Command {
    private final RuntimeMXBean rb;
    private final OperatingSystemMXBean osb;
    private Runtime rt;
    private final CommandManager commandManager;
    private final ShardManager shardManager;

    public StatsCommand(CommandManager commandManager, ShardManager shardManager) {
        name = "stats";
        aliases = new String[] {"botinfo", "about"};

        this.commandManager = commandManager;
        this.shardManager = shardManager;
        rb = ManagementFactory.getRuntimeMXBean();
        osb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        rt = Runtime.getRuntime();
    }

    @Override
    public void execute(@NotNull CommandContext context) {

        long free = rt.freeMemory() / 1024 / 1024;
        long total = rt.totalMemory() / 1024 / 1024;
        long used = total - free;

        AsciiTable at = new AsciiTable();
        at.setTextAlignment(TextAlignment.LEFT);

        at.addRule();
        at.addRow(null, context.getTranslated("about.title"));
        at.addRule();
        at.addRow("Bot version", "v" + Constants.VERSION);
        at.addRow("Library version", "JDA " + JDAInfo.VERSION);
        at.addRow("Author", "gabixdev#0001");
        at.addRow("Guilds", shardManager.getGuilds().size());
        at.addRow("Shard", context.getEvent().getJDA().getShardInfo() + " (" + context.getEvent().getJDA().getGuilds().size() + ")");
        at.addRow("Uptime", StringUtil.prettyPeriod(rb.getUptime()));
        at.addRow("Events received", Globals.eventsSeen);
        at.addRow("Commands executed", commandManager.getRuns());
        at.addRow("Audio connections", context.getEvent().getJDA().getAudioManagers().stream().filter(AudioManager::isConnected).count());
        at.addRow("CPU usage", Math.floor(osb.getProcessCpuLoad() * 100) + "%");
        at.addRow("Memory usage", "Free: " + free + "MB");
        at.addRow("", "Allocated: " + total + "MB");
        at.addRow("", "Used: " + used + "MB");
        at.addRow("", "Last GC: ???");

        at.addRule();

        context.send("```css\n" + at.render(50) + "\n```");
    }
}
