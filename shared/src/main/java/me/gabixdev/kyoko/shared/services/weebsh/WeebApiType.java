package me.gabixdev.kyoko.shared.services.weebsh;

public enum WeebApiType {
    PRODUCTION("https://api.weeb.sh/images/", "https://cdn.weeb.sh/images/"),
    STAGING("https://staging.weeb.sh/images/", "https://cdn.weeb.sh/staging-images/"),
    DEVELOPMENT("http://localhost:9010", "https://cdn.weeb.sh/dev-images/");

    private final String apiUrl;
    private final String cdnUrl;

    private WeebApiType(String apiUrl, String cdnUrl) {
        this.apiUrl = apiUrl;
        this.cdnUrl = cdnUrl;
    }
}
