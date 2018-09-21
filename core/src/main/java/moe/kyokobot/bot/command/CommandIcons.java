package moe.kyokobot.bot.command;

public class CommandIcons {
    public static String WORKING = "⌛  |  ";

    public static String SUCCESS = "✅  |  ";

    public static String ERROR = "⚠  |  ";

    public static String INFO = "ℹ  |  ";

    public static String LEVELUP = "\uD83C\uDD99  |  ";

    static void loadKyokoIcons() {
        WORKING = "<a:working:440090198500573184>  |  ";
        SUCCESS = "<:success:435574370107129867>  |  ";
        ERROR = "<:error:435574504522121216>  |  ";
        INFO = "<:info:435576029680238593>  |  ";
        LEVELUP = "<:levelup:466635901703815188>  |  ";
    }
}
