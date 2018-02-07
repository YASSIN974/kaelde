package me.gabixdev.kyoko.util;

import java.io.*;
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

    public static byte[] readUrlBytes(String urlString) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            URLConnection connection = new URL(urlString).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0");
            connection.connect();
            is = connection.getInputStream();

            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return baos.toByteArray();
    }
}
