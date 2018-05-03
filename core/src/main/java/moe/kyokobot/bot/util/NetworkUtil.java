package moe.kyokobot.bot.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class NetworkUtil {
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0";
    private static OkHttpClient client = new OkHttpClient();

    public static byte[] download(String url) throws IOException {
        Request request = new Request.Builder()
                .header("User-Agent", USER_AGENT)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body() == null ? new byte[0] : response.body().bytes();
    }
}
