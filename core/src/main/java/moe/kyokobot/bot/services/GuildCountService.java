package moe.kyokobot.bot.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.discordapi.DiscordAPI;
import moe.kyokobot.bot.discordapi.event.GuildCountUpdateEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.util.concurrent.TimeUnit;

public class GuildCountService extends AbstractScheduledService {
    private Settings settings;
    private JDA jda;

    private int guilds = 0;

    public GuildCountService(Settings settings, DiscordAPI api) {
        this.settings = settings;
        guilds = jda.getGuilds().size();
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            if (settings.connection.mode.equalsIgnoreCase("gateway")) {
                jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "kgw:gc:" + guilds));
            } else {
                jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT,
                        settings.bot.game
                                .replace("{prefix}", settings.bot.normalPrefix)
                                .replace("{guilds}", String.valueOf(guilds))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.MINUTES);
    }

    @Subscribe
    public void update(GuildCountUpdateEvent event) {
        System.out.println("Guild count update!");
        if (event != null) {
            guilds = event.getGuildCount();
        }
    }
}
