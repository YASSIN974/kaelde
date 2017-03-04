package com.programmingwizzard.charrizard.bot.response;

import com.google.gson.JsonElement;
import com.programmingwizzard.charrizard.util.GsonUtil;
import com.programmingwizzard.charrizard.util.URLUtil;

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
