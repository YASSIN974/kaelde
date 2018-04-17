package me.gabixdev.kyoko;

import com.google.common.util.concurrent.AbstractIdleService;
import me.gabixdev.kyoko.util.KyokoJDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;

public class KyokoService extends AbstractIdleService {
    private Settings settings;
    private JDA jda;

    public KyokoService(Settings settings) {
        this.settings = settings;
    }

    @Override
    protected void startUp() throws Exception {
        KyokoJDABuilder jdaBuilder = new KyokoJDABuilder(AccountType.BOT);
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.setToken(settings.connection.token);

        if (settings.connection.shardMode.equals("gateway")) {

        }
    }

    @Override
    protected void shutDown() throws Exception {

    }
}
