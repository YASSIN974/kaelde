package moe.kyokobot.bot;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import moe.kyokobot.bot.util.ColorTypeAdapter;
import net.dv8tion.jda.core.entities.Game;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Settings {
    public static Settings instance = new Settings();

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

        @SerializedName("shard-string")
        public String shardString = "0:0:1"; // min:max:count
    }

    public class BotSettings {
        @SerializedName("bot-name")
        public String botName = "Kyoko";

        @SerializedName("bot-icon")
        public String botIcon = "<:kyoko:456199924132872213>";

        @SerializedName("owner")
        public String owner = "219067402174988290";

        @SerializedName("normal-prefix")
        public String normalPrefix = "ky!";

        @SerializedName("debug-prefix")
        public String debugPrefix = "kd!";

        @SerializedName("games")
        public List<String> games = Arrays.asList("{prefix}help | {guilds} guilds", "{prefix}help | kyokobot.moe");

        @SerializedName("gametype")
        public Game.GameType gameType = Game.GameType.DEFAULT;

        @SerializedName("normal-color")
        @JsonAdapter(ColorTypeAdapter.class)
        public Color normalColor = new Color(255, 71, 87);

        @SerializedName("error-color")
        @JsonAdapter(ColorTypeAdapter.class)
        public Color errorColor = new Color(231, 76, 60);
    }
}
