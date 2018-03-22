package me.gabixdev.kyoko.shared.services.weebsh;

import com.google.gson.annotations.SerializedName;

public class WeebSettings {
    @SerializedName("token")
    public String token = "ask wolke for ur token";

    @SerializedName("token-type")
    public WeebTokenType tokenType = WeebTokenType.WOLKE;

    @SerializedName("api-type")
    public WeebApiType apiType = WeebApiType.DEVELOPMENT;
}
