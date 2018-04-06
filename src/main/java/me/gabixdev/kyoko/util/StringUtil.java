package me.gabixdev.kyoko.util;

import me.gabixdev.kyoko.Kyoko;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StringUtil {
    static Map<String, String> katakana = new HashMap<String, String>();

    static {
        katakana.put("ア", "a");
        katakana.put("イ", "i");
        katakana.put("ウ", "u");
        katakana.put("エ", "e");
        katakana.put("オ", "o");
        katakana.put("カ", "ka");
        katakana.put("キ", "ki");
        katakana.put("ク", "ku");
        katakana.put("ケ", "ke");
        katakana.put("コ", "ko");
        katakana.put("サ", "sa");
        katakana.put("シ", "shi");
        katakana.put("ス", "su");
        katakana.put("セ", "se");
        katakana.put("ソ", "so");
        katakana.put("タ", "ta");
        katakana.put("チ", "chi");
        katakana.put("ツ", "tsu");
        katakana.put("テ", "te");
        katakana.put("ト", "to");
        katakana.put("ナ", "na");
        katakana.put("ニ", "ni");
        katakana.put("ヌ", "nu");
        katakana.put("ネ", "ne");
        katakana.put("ノ", "no");
        katakana.put("ハ", "ha");
        katakana.put("ヒ", "hi");
        katakana.put("フ", "fu");
        katakana.put("ヘ", "he");
        katakana.put("ホ", "ho");
        katakana.put("マ", "ma");
        katakana.put("ミ", "mi");
        katakana.put("ム", "mu");
        katakana.put("メ", "me");
        katakana.put("モ", "mo");
        katakana.put("ヤ", "ya");
        katakana.put("ユ", "yu");
        katakana.put("ヨ", "yo");
        katakana.put("ラ", "ra");
        katakana.put("リ", "ri");
        katakana.put("ル", "ru");
        katakana.put("レ", "re");
        katakana.put("ロ", "ro");
        katakana.put("ワ", "wa");
        katakana.put("ヲ", "wo");
        katakana.put("ン", "n");
        katakana.put("ガ", "ga");
        katakana.put("ギ", "gi");
        katakana.put("グ", "gu");
        katakana.put("ゲ", "ge");
        katakana.put("ゴ", "go");
        katakana.put("ザ", "za");
        katakana.put("ジ", "ji");
        katakana.put("ズ", "zu");
        katakana.put("ゼ", "ze");
        katakana.put("ゾ", "zo");
        katakana.put("ダ", "da");
        katakana.put("ヂ", "ji");
        katakana.put("ヅ", "zu");
        katakana.put("デ", "de");
        katakana.put("ド", "do");
        katakana.put("バ", "ba");
        katakana.put("ビ", "bi");
        katakana.put("ブ", "bu");
        katakana.put("ベ", "be");
        katakana.put("ボ", "bo");
        katakana.put("パ", "pa");
        katakana.put("ピ", "pi");
        katakana.put("プ", "pu");
        katakana.put("ペ", "pe");
        katakana.put("ポ", "po");
        katakana.put("キャ", "kya");
        katakana.put("キュ", "kyu");
        katakana.put("キョ", "kyo");
        katakana.put("シャ", "sha");
        katakana.put("シュ", "shu");
        katakana.put("ショ", "sho");
        katakana.put("チャ", "cha");
        katakana.put("チュ", "chu");
        katakana.put("チョ", "cho");
        katakana.put("ニャ", "nya");
        katakana.put("ニュ", "nyu");
        katakana.put("ニョ", "nyo");
        katakana.put("ヒャ", "hya");
        katakana.put("ヒュ", "hyu");
        katakana.put("ヒョ", "hyo");
        katakana.put("リャ", "rya");
        katakana.put("リュ", "ryu");
        katakana.put("リョ", "ryo");
        katakana.put("ギャ", "gya");
        katakana.put("ギュ", "gyu");
        katakana.put("ギョ", "gyo");
        katakana.put("ジャ", "ja");
        katakana.put("ジュ", "ju");
        katakana.put("ジョ", "jo");
        katakana.put("ティ", "ti");
        katakana.put("ディ", "di");
        katakana.put("ツィ", "tsi");
        katakana.put("ヂャ", "dya");
        katakana.put("ヂュ", "dyu");
        katakana.put("ヂョ", "dyo");
        katakana.put("ビャ", "bya");
        katakana.put("ビュ", "byu");
        katakana.put("ビョ", "byo");
        katakana.put("ピャ", "pya");
        katakana.put("ピュ", "pyu");
        katakana.put("ピョ", "pyo");
        katakana.put("ー", "-");
    }

    public static int getOccurencies(String string, String subString) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = string.indexOf(subString, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += subString.length();
            }
        }

        return count;
    }

    public static String prettyPeriod(long millis) {
        if (millis == Long.MAX_VALUE) return "streaming";
        // because java builtin methods sucks...

        final long secs = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        final long mins = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);

        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }

    public static String katakana2romaji(String in) {
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            if (i <= in.length() - 2) {
                if (katakana.containsKey(in.substring(i, i + 2))) {
                    t.append(katakana.get(in.substring(i, i + 2)));
                    i++;
                } else if (katakana.containsKey(in.substring(i, i + 1))) {
                    t.append(katakana.get(in.substring(i, i + 1)));
                } else if (in.charAt(i) == 'ッ') {
                    t.append(katakana.get(in.substring(i + 1, i + 2)).charAt(0));
                } else {
                    t.append(in.charAt(i));
                }
            } else {
                if (katakana.containsKey(in.substring(i, i + 1))) {
                    t.append(katakana.get(in.substring(i, i + 1)));
                } else {
                    t.append(in.charAt(i));
                }
            }
        }
        return t.toString();
    }

    public static String stripPrefix(Kyoko kyoko, String label, String msg) {
        String mention = kyoko.getJda().getSelfUser().getAsMention();
        if (msg.startsWith(mention)) {
            return msg.substring(mention.length() + label.length() + 2);
        } else {
            return msg.substring(kyoko.getSettings().getPrefix().length() + label.length() + 1);
        }
    }

    public static String logUser(User author) {
        return author.getName() + " (" + author.getId() + ")";
    }
}