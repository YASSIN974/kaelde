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

    @SerializedName("token")
    private String token = "Change me";

    @SerializedName("game")
    private String game = "dev";

    @SerializedName("game-url")
    private String gameUrl = "https://gabixdev.me";

    private String prefix = "k!";

    @SerializedName("blinking-shit")
    private String blinkingShit = "none";

    @SerializedName("normal-color")
    @JsonAdapter(ColorTypeAdapter.class)
    private Color normalColor = new Color(46, 204, 113);

    @SerializedName("error-color")
    @JsonAdapter(ColorTypeAdapter.class)
    private Color errorColor = new Color(231, 76, 60);

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

    public String getBlinkingShit() {
        return blinkingShit;
    }

    public Color getNormalColor() {
        return normalColor;
    }

    public Color getErrorColor() {
        return errorColor;
    }
}
