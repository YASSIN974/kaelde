package moe.kyokobot.bot.discordapi.impl;

import com.google.common.eventbus.EventBus;
import moe.kyokobot.bot.discordapi.DiscordAPI;
import moe.kyokobot.bot.discordapi.entity.Guild;
import moe.kyokobot.bot.discordapi.entity.TextChannel;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.util.HashMap;

import static net.dv8tion.jda.core.AccountType.BOT;

public class JDADiscordAPI implements DiscordAPI {
    private final String token;
    private JDAEventHandler jdaEventHandler;
    private JDA jda;
    private HashMap<Long, Guild> guildCache;

    public JDADiscordAPI(String token, EventBus eventBus) {
        this.token = token;
        jdaEventHandler = new JDAEventHandler(eventBus, this);
        guildCache = new HashMap<>();
    }

    @Override
    public void initialize() throws Exception {
        JDABuilder jb = new JDABuilder(BOT);
        jb.setAutoReconnect(true);
        jb.setToken(token);
        jb.addEventListener(jdaEventHandler);
        jda = jb.buildBlocking();
    }

    @Override
    public void shutdown() {
        if (jda != null) jda.shutdown();
    }

    @Override
    public Guild getGuild(long id) {
        if (guildCache.containsKey(id)) {
            return guildCache.get(id);
        }
        return null;
    }

    @Override
    public TextChannel getChannel(Guild g, long id) {
        return null;
    }

    @Override
    public void updateGuild(long id, Guild guild) {

    }
}
