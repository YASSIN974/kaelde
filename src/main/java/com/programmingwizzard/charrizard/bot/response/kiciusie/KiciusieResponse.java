package com.programmingwizzard.charrizard.bot.response.kiciusie;

import com.google.gson.JsonElement;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class KiciusieResponse {

    private final String imageUrl;

    public KiciusieResponse(JsonElement element) {
        imageUrl = element.getAsJsonObject().get("url").getAsString();
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
