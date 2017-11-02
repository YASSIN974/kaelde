package me.gabixdev.kyoko.remoteapi.kiciusie;

import me.gabixdev.kyoko.remoteapi.Response;
import me.gabixdev.kyoko.remoteapi.ResponseException;

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
