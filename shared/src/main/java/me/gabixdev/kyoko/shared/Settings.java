package me.gabixdev.kyoko.shared;

import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("owner")
    public String owner = "219067402174988290";

    @SerializedName("devs")
    public String devs = "219067402174988290";

    @SerializedName("token")
    public String token = "paste token here";

    @SerializedName("manifest-url")
    public String manifestUrl = "https://kyoko.gabixdev.me/release.json";

    @SerializedName("shard-mode")
    public String shardMode = "single"; // single, main, slave

    @SerializedName("shard-id")
    public int shardId = -1;
}
