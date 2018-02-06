package me.gabixdev.kyoko.music;

public class SearchEntry {
    private final String title;
    private final String url;

    public SearchEntry(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getURL() {
        return url;
    }
}