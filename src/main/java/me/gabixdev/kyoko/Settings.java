package me.gabixdev.kyoko;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import me.gabixdev.kyoko.util.ColorTypeAdapter;

import java.awt.*;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
@SuppressWarnings("FieldCanBeLocal")
public class Settings {

    @SerializedName("owner")
    private String owner = "219067402174988290";

    @SerializedName("token")
    private String token = "Change me";

    @SerializedName("game")
    private String game = "development";

    @SerializedName("game-url")
    private String gameUrl = "https://kyoko.gabixdev.me";

    private String prefix = "ky!";

    @SerializedName("wip-features")
    private boolean wipFeaturesEnabled = false;

    @SerializedName("blinking-shit")
    private String blinkingShit = "none";

    @SerializedName("allow-unsafe-sources")
    private boolean allowUnsafeSources = false;

    @SerializedName("normal-color")
    @JsonAdapter(ColorTypeAdapter.class)
    private Color normalColor = new Color(230, 126, 34);

    @SerializedName("success-color")
    @JsonAdapter(ColorTypeAdapter.class)
    private Color successColor = new Color(46, 204, 113);

    @SerializedName("error-color")
    @JsonAdapter(ColorTypeAdapter.class)
    private Color errorColor = new Color(231, 76, 60);

    @SerializedName("enable-nicovideo")
    private boolean nicoEnabled = false;

    @SerializedName("enable-ytsearch")
    private boolean youtubeSearchEnabled = false;

    @SerializedName("nico-mail")
    private String nicoMail = "nico@example.com";

    @SerializedName("nico-password")
    private String nicoPassword = "password";

    @SerializedName("youtube-apikey")
    private String youtubeApiKey = "nico@example.com";

    @SerializedName("min-remove")
    private int minRemove = 2;

    @SerializedName("max-remove")
    private int maxRemove = 1000;

    public String getOwner() {
        return owner;
    }

    public String getToken() {
        return token;
    }

    public String getGame() {
        return game;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isWipFeaturesEnabled() {
        return wipFeaturesEnabled;
    }

    public String getBlinkingShit() {
        return blinkingShit;
    }

    public boolean isAllowUnsafeSources() {
        return allowUnsafeSources;
    }

    public Color getNormalColor() {
        return normalColor;
    }

    public Color getSuccessColor() {
        return successColor;
    }

    public Color getErrorColor() {
        return errorColor;
    }

    public boolean isNicovideoEnabled() {
        return nicoEnabled;
    }

    public String getNicoMail() {
        return nicoMail;
    }

    public String getNicoPassword() {
        return nicoPassword;
    }

    public boolean isYoutubeSearchEnabled() {
        return youtubeSearchEnabled;
    }

    public String getYoutubeApiKey() {
        return youtubeApiKey;
    }

    public int getMinRemove() {
        return minRemove;
    }

    public int getMaxRemove() {
        return maxRemove;
    }
}
