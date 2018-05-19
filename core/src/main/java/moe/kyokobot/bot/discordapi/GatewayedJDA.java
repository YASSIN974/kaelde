package moe.kyokobot.bot.discordapi;

import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.requests.WebSocketClient;
import net.dv8tion.jda.core.utils.SessionController;
import okhttp3.OkHttpClient;
import org.slf4j.MDC;

import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class GatewayedJDA extends JDAImpl {
    public GatewayedJDA(AccountType accountType, String token, SessionController controller, OkHttpClient.Builder httpClientBuilder, WebSocketFactory wsFactory, boolean autoReconnect, boolean audioEnabled, boolean useShutdownHook, boolean bulkDeleteSplittingEnabled, boolean retryOnTimeout, boolean enableMDC, int corePoolSize, int maxReconnectDelay, ConcurrentMap<String, String> contextMap) {
        super(accountType, token, controller, httpClientBuilder, wsFactory, autoReconnect, audioEnabled, useShutdownHook, bulkDeleteSplittingEnabled, retryOnTimeout, enableMDC, corePoolSize, maxReconnectDelay, contextMap);
    }

    @Override
    public int login(String amqpUrl, ShardInfo shardInfo, boolean compression) throws LoginException {
        this.gatewayUrl = amqpUrl;
        this.shardInfo = shardInfo;

        setStatus(Status.LOGGING_IN);
        if (token == null || token.isEmpty())
            throw new LoginException("Provided token was null or empty!");

        Map<String, String> previousContext = null;
        if (contextMap != null)
        {
            if (shardInfo != null)
            {
                contextMap.put("jda.shard", shardInfo.getShardString());
                contextMap.put("jda.shard.id", String.valueOf(shardInfo.getShardId()));
                contextMap.put("jda.shard.total", String.valueOf(shardInfo.getShardTotal()));
            }
            // set MDC metadata for build thread
            previousContext = MDC.getCopyOfContextMap();
            contextMap.forEach(MDC::put);
        }
        verifyToken();
        LOG.info("Login Successful!");

        client = new GatewayedWebSocketClient(this, true);

        if (previousContext != null)
            previousContext.forEach(MDC::put);

        if (shutdownHook != null)
            Runtime.getRuntime().addShutdownHook(shutdownHook);

        return shardInfo == null ? -1 : shardInfo.getShardTotal();
    }
}
