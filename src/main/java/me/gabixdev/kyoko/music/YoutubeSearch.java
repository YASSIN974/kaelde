package me.gabixdev.kyoko.music;

import com.google.gson.JsonObject;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.exception.APIException;
import me.gabixdev.kyoko.util.exception.NotFoundException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YoutubeSearch {
    private static String API_URL;

    public YoutubeSearch(Kyoko kyoko) {
        API_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key=" + kyoko.getSettings().getYoutubeApiKey() + "&q=";
    }

    public static SearchResult search(String query) throws UnsupportedEncodingException, IOException, NotFoundException, APIException {
        query = URLEncoder.encode(query, "UTF-8");
        String data = URLUtil.readUrl(API_URL + query);
        JsonObject out = GsonUtil.fromStringToJsonElement(data).getAsJsonObject();

        SearchResult searchResult = new SearchResult();

        if (out.has("error")) {
            throw new APIException("Request error", data);
        } else {
            try {
                out.getAsJsonArray("items").forEach(jsonElement -> {
                    JsonObject o = jsonElement.getAsJsonObject();
                    if (o.get("id").getAsJsonObject().get("kind").getAsString().equals("youtube#video")) {
                        String videoId = o.get("id").getAsJsonObject().get("videoId").getAsString();
                        String title = o.get("snippet").getAsJsonObject().get("title").getAsString();
                        searchResult.addEntry(title, "https://youtube.com/watch?v=" + videoId);
                    }
                });
            } catch (Exception ex) {
                throw new APIException(ex.getClass().getCanonicalName() + ": " + ex.getMessage(), data);
            }
        }

        return searchResult;
    }
}
