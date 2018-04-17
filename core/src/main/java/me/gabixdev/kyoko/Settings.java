package me.gabixdev.kyoko;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.gabixdev.kyoko.util.ColorTypeAdapter;

import java.awt.*;
import java.util.HashMap;

public class Settings {
    @SerializedName("bot")
    public BotSettings bot = new BotSettings();

    @SerializedName("connection")
    public ConnectionSettings connection = new ConnectionSettings();

    @SerializedName("apikeys")
    public HashMap<String, String> apiKeys = new HashMap<>();

    public class ConnectionSettings {
        @SerializedName("token")
        public String token = "";

        @SerializedName("database-url")
        public String databaseUrl = "jdbc:mysql://localhost:3306/<database>?user=<username>&password=<password>&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        @SerializedName("gateway-server")
        public String gatewayServer = "ws://localhost:8000";

        @SerializedName("rest-server")
        public String restServer = "http://localhost:9000/";

        @SerializedName("mode")
        public String shardMode = "single"; // single, gateway

        @SerializedName("shard-id")
        public int shardId = 0;
    }

    public class BotSettings {
        @SerializedName("bot-name")
        public String botName = "Kyoko";

        @SerializedName("owner")
        public String owner = "219067402174988290";

        @SerializedName("devs")
        public String devs = "219067402174988290";

        @SerializedName("normal-prefix")
        public String normalPrefix = "ky!";

        @SerializedName("moderation-prefix")
        public String moderationPrefix = "ky@";

        @SerializedName("debug-prefix")
        public String debugPrefix = "kd!";

        @SerializedName("normal-color")
        @JsonAdapter(ColorTypeAdapter.class)
        public Color normalColor = new Color(201, 145, 84);

        @SerializedName("success-color")
        @JsonAdapter(ColorTypeAdapter.class)
        public Color successColor = new Color(46, 204, 113);

        @SerializedName("error-color")
        @JsonAdapter(ColorTypeAdapter.class)
        public Color errorColor = new Color(231, 76, 60);
    }
}
