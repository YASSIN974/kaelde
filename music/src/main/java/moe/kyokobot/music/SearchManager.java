package moe.kyokobot.music;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import lombok.Getter;
import moe.kyokobot.bot.util.GsonUtil;
import moe.kyokobot.bot.util.NetworkUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchManager {
    private final String youtubeApiUrl;

    private Cache<String, SearchResult> youtubeResults;

    public SearchManager(String youtubeApiKey) {
        youtubeApiUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&key=" + youtubeApiKey + "&q=";
        youtubeResults = Caffeine.newBuilder().expireAfterWrite(90, TimeUnit.MINUTES).maximumSize(2000).build();
    }

    public SearchResult searchYouTube(String query) {
        SearchResult result = youtubeResults.getIfPresent(query.toLowerCase());
        if (result != null) {
            return result;
        } else {
            try {
                query = URLEncoder.encode(query, "UTF-8");
                String data = new String(NetworkUtil.download(youtubeApiUrl + query), Charsets.UTF_8);
                JsonObject element = GsonUtil.gson.fromJson(data, JsonObject.class);


                SearchResult jresult = new SearchResult();

                if (element.has("error")) {
                    throw new IllegalStateException("Request error: " + data);
                } else {
                    try {
                        element.getAsJsonArray("items").forEach(jsonElement -> {
                            JsonObject o = jsonElement.getAsJsonObject();
                            if (o.get("id").getAsJsonObject().get("kind").getAsString().equals("youtube#video")) {
                                String videoId = o.get("id").getAsJsonObject().get("videoId").getAsString();
                                String title = o.get("snippet").getAsJsonObject().get("title").getAsString();
                                jresult.addEntry(title, "https://youtube.com/watch?v=" + videoId);
                            }
                        });
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex.getClass().getCanonicalName() + ": " + ex.getMessage() + ": " + data);
                    }
                }
                return jresult;
            } catch (Exception e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }
        return null;
    }

    public String getDebug() {
        StringBuilder sb = new StringBuilder();
        sb.append("SearchManager\n");
        sb.append("----------------\n");
        sb.append("Cached YouTube results: ").append(youtubeResults.asMap().size()).append("\n");
        return sb.toString();
    }

    public class SearchResult {
        private ArrayList<SearchEntry> entries;

        public SearchResult() {
            entries = new ArrayList<>();
        }

        protected void addEntry(String title, String url) {
            entries.add(new SearchEntry(title, url));
        }

        public List<SearchEntry> getEntries() {
            return entries;
        }
    }

    @Getter
    public class SearchEntry {
        private final String title;
        private final String url;

        public SearchEntry(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }
}
