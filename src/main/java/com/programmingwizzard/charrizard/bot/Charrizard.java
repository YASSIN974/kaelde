package com.programmingwizzard.charrizard.bot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.programmingwizzard.charrizard.bot.command.HelpCommand;
import com.programmingwizzard.charrizard.bot.command.basic.DiscordCommands;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import pl.themolka.commons.command.Commands;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class Charrizard {

    private final Settings settings;
    private final Commands commands;

    private final Cache<String, ExecutorService> poolCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    private JDA jda;

    public Charrizard(Settings settings) {
        this.settings = settings;
        this.commands = new DiscordCommands(this);
    }

    public void start() throws LoginException, InterruptedException, RateLimitedException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        if (settings.getToken() != null) {
            builder.setToken(settings.getToken());
        }
        if (settings.getGame() != null) {
            builder.setGame(Game.of(settings.getGame(), settings.getGameUrl()));
        }
        builder.setAutoReconnect(true);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.addListener((DiscordCommands) commands);
        builder.setAudioEnabled(false);
        builder.setStatus(OnlineStatus.ONLINE);
        jda = builder.buildBlocking();

        commands();
    }

    private void commands() {
        commands.registerCommandObjects(
                new HelpCommand(this)
        );
    }

    public void run(Guild guild, Runnable runnable) {
        if (guild == null || runnable == null) {
            return;
        }
        ExecutorService service = poolCache.getIfPresent(guild.getId());
        if (service == null) {
            service = new ThreadPoolExecutor(2, 16, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
            poolCache.put(guild.getId(), service);
        }
        service.execute(runnable);
    }

    public Commands getCommands() {
        return commands;
    }
}
