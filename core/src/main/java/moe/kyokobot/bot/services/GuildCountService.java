package moe.kyokobot.bot.services;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import io.sentry.Sentry;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.event.GuildCountUpdateEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.util.concurrent.TimeUnit;

public class GuildCountService extends AbstractScheduledService {
    private Settings settings;
    private JDA jda;

    private int guilds = 0;

    public GuildCountService(Settings settings, JDA jda) {
        this.settings = settings;
        this.jda = jda;

        guilds = jda.getGuilds().size();
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            if (settings.connection.mode.equalsIgnoreCase("gateway")) {
                jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "kgw:gc:" + guilds));
            } else {
                jda.getPresence().setGame(Game.of(settings.bot.gameType,
                        settings.bot.game
                                .replace("{prefix}", settings.bot.normalPrefix)
                                .replace("{guilds}", String.valueOf(guilds))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 5, TimeUnit.MINUTES);
    }

    @Subscribe
    public void update(GuildCountUpdateEvent event) {
        if (event != null) {
            guilds = event.getGuildCount();
        }
    }
}
