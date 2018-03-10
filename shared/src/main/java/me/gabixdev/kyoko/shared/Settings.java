package me.gabixdev.kyoko.shared;

import com.google.gson.annotations.SerializedName;

public class Settings {
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
}
