package me.gabixdev.kyoko.shared;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.gabixdev.kyoko.shared.services.weebsh.WeebSettings;
import me.gabixdev.kyoko.shared.util.ColorTypeAdapter;

import java.awt.*;

public class Settings {
    @SerializedName("owner")
    public String owner = "219067402174988290";

    @SerializedName("devs")
    public String devs = "219067402174988290";

    @SerializedName("token")
    public String token = "paste token here";

    @SerializedName("normal-prefix")
    public String normalPrefix = "ky!";

    @SerializedName("moderation-prefix")
    public String moderationPrefix = "ky@";

    @SerializedName("debug-prefix")
    public String debugPrefix = "kd!";

    @SerializedName("shard")
    public ShardSettings shard = new ShardSettings();

    @SerializedName("brand")
    public BrandSettings brand = new BrandSettings();

    @SerializedName("weebsh")
    public WeebSettings weebSettings = new WeebSettings();


    public class ShardSettings {
        @SerializedName("data-server")
        public String dataServer = "localhost:3000";

        @SerializedName("shard-mode")
        public String shardMode = "single"; // single, main, slave

        @SerializedName("shard-id")
        public int shardId = -1;

        @SerializedName("shard-count")
        public int shardCount = -1;

    }

    public class BrandSettings {
        @SerializedName("bot-name")
        public String botName = "Kyoko";

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
