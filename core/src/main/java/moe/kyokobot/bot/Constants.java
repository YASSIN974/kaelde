package moe.kyokobot.bot;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class Constants {
    private Constants() {

    }

    public static boolean DEBUG = false;
    public static final String VERSION;
    public static final String SITE_URL = "https://kyokobot.moe";
    public static final String SITE_URL_MD = "[kyokobot.moe](https://kyokobot.moe)";
    public static final String GITHUB_URL = "https://github.com/KyokoBot/";
    public static final String GITHUB_URL_MD = "[KyokoBot](https://github.com/KyokoBot)";
    public static final String COMMANDS_URL = "https://kyokobot.moe/commands";
    public static final String DISCORD_URL = "https://discord.gg/ZvDRQf7";
    public static final String DISCORDBOTS_URL = "https://discordbots.org/bot/375750637540868107";
    public static final int PERMISSIONS = 2117598326;

    public static final String POWERED_BY_WEEB = "powered by weeb.sh";
    public static final String POWERED_BY_DOGCEO = "powered by dog.ceo";
    public static final String POWERED_BY_NEKOSLIFE = "powered by nekos.life";
    public static final String POWERED_BY_ALEX = "powered by AlexFlipnote's API";
    public static final String POWERED_BY_LOLISLIFE = "powered by lolis.life";
    public static final String POWERED_BY_CF = "powered by api.computerfreaker.cf";

    public static final List<String> BOTLIST_GUILDS = Collections.unmodifiableList(Lists.newArrayList(
            "110373943822540800", // Discord Bots
            "264445053596991498", // Discord Bot List
            "459854572534366208", // ListCord
            "387812458661937152"  // botlist.space
    ));

    static {
        VERSION = "2.0.0";
    }
}