package me.gabixdev.kyoko.shared.services.weebsh;

public class WeebApi {
    private WeebTokenType tokenType;
    private String token;

    public WeebApi(WeebTokenType tokenType, String token) {
        this.tokenType = tokenType;
        this.token = token;
    }

    public void setTokenType(WeebTokenType tokenType) {
        this.tokenType = tokenType;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
