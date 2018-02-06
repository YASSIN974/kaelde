package me.gabixdev.kyoko.music;

import java.util.ArrayList;

public class SearchResult {
    private ArrayList<SearchEntry> entries;

    public SearchResult() {
        entries = new ArrayList<>();
    }

    public void addEntry(String title, String url) {
        entries.add(new SearchEntry(title, url));
    }

    public ArrayList<SearchEntry> getEntries() {
        return entries;
    }
}
