package me.gabixdev.kyoko.remoteapi;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
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
