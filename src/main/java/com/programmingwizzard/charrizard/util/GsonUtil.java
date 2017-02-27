package com.programmingwizzard.charrizard.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class GsonUtil {

    private final static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    public static <T> T readConfiguration(Class<T> configurationClass, File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
            Files.write(file.toPath(), gson.toJson(configurationClass.newInstance()).getBytes(StandardCharsets.UTF_8));
        }
        return gson.fromJson(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8), configurationClass);
    }

    public static <T> T readObjectFromFile(Class<T> object, File file) throws Exception {
        if (!file.exists()) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return gson.fromJson(reader, object);
    }

    public static void writeObjectToFile(Object object, File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        writer.write(gson.toJson(object));
        writer.close();
    }

    public static JsonElement fromStringToJsonElement(String json) {
        return gson.fromJson(json, JsonElement.class);
    }

    public static String fromJsonElementToString(JsonElement element) {
        return gson.toJson(element);
    }
}