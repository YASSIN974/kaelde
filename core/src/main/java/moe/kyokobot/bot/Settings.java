package moe.kyokobot.bot;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import moe.kyokobot.bot.util.ColorTypeAdapter;
import net.dv8tion.jda.core.entities.Game;

import java.awt.*;
import java.util.HashMap;

public class Settings {
    @SerializedName("debug")
    public boolean debug = false;

    @SerializedName("connection")
    public ConnectionSettings connection = new ConnectionSettings();

    @SerializedName("bot")
    public BotSettings bot = new BotSettings();

    @SerializedName("apikeys")
    public HashMap<String, String> apiKeys = new HashMap<>();

    @SerializedName("apiurls")
    public HashMap<String, String> apiUrls = new HashMap<>();

    public class ConnectionSettings {
        /*public ConnectionSettings() {

        }

        public ConnectionSettings(String token, String rethinkHost, int rethinkPort, String rethinkUser, String rethinkPassword, String rethinkDbName, String gatewayServer, String restServer, String mode, int shardCount) {
            this.token = token;
            this.rethinkHost = rethinkHost;
            this.rethinkPort = rethinkPort;
            this.rethinkUser = rethinkUser;
            this.rethinkPassword = rethinkPassword;
            this.rethinkDbName = rethinkDbName;
            this.gatewayServer = gatewayServer;
            this.restServer = restServer;
            this.mode = mode;
            this.shardCount = shardCount;
        }*/

        @SerializedName("token")
        public String token = "";

        @SerializedName("rethink-host")
        public String rethinkHost = "localhost";

        @SerializedName("rethink-port")
        public int rethinkPort = 28015;

        @SerializedName("rethink-user")
        public String rethinkUser = "admin";

        @SerializedName("rethink-password")
        public String rethinkPassword = "";

        @SerializedName("rethink-dbname")
        public String rethinkDbName = "kyoko";

        @SerializedName("gateway-server")
        public String gatewayServer = "ws://localhost:8000";

        @SerializedName("rest-server")
        public String restServer = "http://localhost:9000/api/";

        @SerializedName("mode")
        public String mode = "single"; // single, gateway

        @SerializedName("shard-count")
        public int shardCount = 0;
    }

    public class BotSettings {
        @SerializedName("bot-name")
        public String botName = "Kyoko";

        @SerializedName("bot-icon")
        public String botIcon = "<:kyoko:440219494267355146>";

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

        @SerializedName("game")
        public String game = "{prefix}help | {guilds} guilds";

        @SerializedName("gametype")
        public Game.GameType gameType = Game.GameType.DEFAULT;

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
