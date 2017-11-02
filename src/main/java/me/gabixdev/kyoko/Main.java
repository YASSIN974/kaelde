package me.gabixdev.kyoko;

import me.gabixdev.kyoko.util.GsonUtil;

import java.io.File;

/*
 * @author ProgrammingWizzard
 * @date 04.02.2017
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Settings settings = GsonUtil.readConfiguration(Settings.class, new File("config.json"));
        Kyoko kyoko = new Kyoko(settings);
        kyoko.start();
    }
}
