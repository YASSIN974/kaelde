package com.programmingwizzard.charrizard.bot.response;

/*
 * @author Libter
 * @date 11.02.2017
 */
public class ResponseException extends Exception {

    private final String url;

    public String getUrl() {
        return url;
    }

    public ResponseException(String url) {
        this.url = url;
    }

}
