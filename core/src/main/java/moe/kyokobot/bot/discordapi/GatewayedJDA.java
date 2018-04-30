package moe.kyokobot.bot.discordapi;

import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.utils.SessionController;
import okhttp3.OkHttpClient;

import java.util.concurrent.ConcurrentMap;

public class GatewayedJDA extends JDAImpl {

    public GatewayedJDA(AccountType accountType, String token, SessionController controller, OkHttpClient.Builder httpClientBuilder, WebSocketFactory wsFactory, boolean autoReconnect, boolean audioEnabled, boolean useShutdownHook, boolean bulkDeleteSplittingEnabled, boolean retryOnTimeout, boolean enableMDC, int corePoolSize, int maxReconnectDelay, ConcurrentMap<String, String> contextMap) {
        super(accountType, token, controller, httpClientBuilder, wsFactory, autoReconnect, audioEnabled, useShutdownHook, bulkDeleteSplittingEnabled, retryOnTimeout, enableMDC, corePoolSize, maxReconnectDelay, contextMap);
    }
}
