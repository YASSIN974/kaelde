package me.gabixdev.kyoko.remoteapi;

import com.google.gson.JsonElement;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;

import java.io.IOException;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class Response {

    public static JsonElement getJson(String url) throws ResponseException {
        try {
            return GsonUtil.fromStringToJsonElement(URLUtil.readUrl(url));
        } catch (IOException ex) {
            throw new ResponseException(url);
        }
    }

}
