package me.gabixdev.kyoko.shared;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.gabixdev.kyoko.shared.util.ColorTypeAdapter;

import java.awt.*;

public class Settings {
    @SerializedName("bot-brand")
    public String botBrand = "Kyoko";

    @SerializedName("owner")
    public String owner = "219067402174988290";

    @SerializedName("devs")
    public String devs = "219067402174988290";

    @SerializedName("token")
    public String token = "paste token here";

    @SerializedName("update-download-url")
    public String updateDownloadUrl = "";

    @SerializedName("normal-prefix")
    public String normalPrefix = "ky!";

    @SerializedName("moderation-prefix")
    public String moderationPrefix = "ky@";

    @SerializedName("debug-prefix")
    public String debugPrefix = "kd!";

    @SerializedName("shard-mode")
    public String shardMode = "single"; // single, main, slave

    @SerializedName("shard-id")
    public int shardId = -1;

    @SerializedName("shard-count")
    public int shardCount = -1;

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
