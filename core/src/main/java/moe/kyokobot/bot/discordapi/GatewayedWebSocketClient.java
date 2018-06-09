package moe.kyokobot.bot.discordapi;

import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.requests.WebSocketClient;

public class GatewayedWebSocketClient extends WebSocketClient {
    public GatewayedWebSocketClient(JDAImpl api, boolean compression) {
        super(api, compression);
    }

    @Override
    protected boolean send(String message, boolean skipQueue) {
        return false;
    }

    @Override
    protected synchronized void connect() {

    }

    @Override
    public void close() {
        // nothing
    }

    @Override
    public void close(int code) {
        // nothing
    }

    @Override
    public void close(int code, String reason) {
        // nothing
    }
}
