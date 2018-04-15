package me.gabixdev.kyoko.gateway;

import me.gabixdev.kyoko.Kyoko;

public class GatewayData {
    private final Kyoko kyoko;
    public GatewayData(Kyoko kyoko) {
        this.kyoko = kyoko;
    }

    public String getCurrentShardString() {
        return "[0/1]";
    }
}
