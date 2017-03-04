package com.programmingwizzard.charrizard.bot.response.kiciusie;

import com.programmingwizzard.charrizard.bot.response.Response;
import com.programmingwizzard.charrizard.bot.response.ResponseException;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class KiciusieResponses {

    public static String API = "https://api.kiciusie.pl/?type=get&mode=%s";

    public KiciusieResponse call(KiciusieMode mode) throws ResponseException {
        return new KiciusieResponse(Response.getJson(
                String.format(API, mode.name().toLowerCase())));
    }

}
