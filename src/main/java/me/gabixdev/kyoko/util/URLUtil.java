package me.gabixdev.kyoko.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/*
 * @author ProgrammingWizzard
 * @date 03.03.2017
 */
public class URLUtil {

    public static String readUrl(String urlString) throws IOException {
        BufferedReader reader = null;
        try {
            URLConnection connection = new URL(urlString).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0");
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
