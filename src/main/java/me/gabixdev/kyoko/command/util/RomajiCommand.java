package me.gabixdev.kyoko.command.util;

import com.atilika.kuromoji.naist.jdic.Token;
import com.atilika.kuromoji.naist.jdic.Tokenizer;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.List;

public class RomajiCommand extends Command {
    private final String mention;
    private final String name;
    private final String[] aliases = new String[]{"romaji"};
    private Tokenizer tokenizer;
    private Kyoko kyoko;

    public RomajiCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        mention = kyoko.getJda().getSelfUser().getAsMention();
        name = kyoko.getJda().getSelfUser().getName();

        tokenizer = new Tokenizer.Builder().build();
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "romaji.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.UTILITY;
    }

    @Override
    public String getUsage() {
        return "romaji.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        if (args.length == 1) {
            printUsage(kyoko, l, message.getTextChannel());
        } else {
            String msg = message.getContentDisplay();
            if (message.getContentRaw().startsWith(mention)) {
                msg = msg.substring(name.length()).trim().substring(args[0].length()).trim().substring(args[1].length());
            } else {
                msg = msg.substring(kyoko.getSettings().getPrefix().length() + args[0].length()).trim().substring(args[1].length());
            }

            // https://github.com/nicolas-raoul/jakaroma

            StringBuffer sb = new StringBuffer();
            String lastTokenToMerge = "";

            List<Token> tokens = tokenizer.tokenize(msg);

            for (Token token : tokens) {
                if (token.getAllFeaturesArray()[0].equals("記号")) {
                    sb.append(token.getSurface());
                    continue;
                }
                switch (token.getAllFeaturesArray()[1]) {
                    case "数":
                    case "アルファベット":
                    case "サ変接続":
                        sb.append(token.getSurface());
                        continue;
                    default:
                        String lastFeature = token.getAllFeaturesArray()[8];
                        if (lastFeature.equals("*")) {
                            sb.append(token.getSurface());
                        } else {
                            String romaji = StringUtil.katakana2romaji(token.getAllFeaturesArray()[8]);

                            if (lastFeature.endsWith("ッ")) {
                                lastTokenToMerge = lastFeature;
                                continue;
                            } else {
                                lastTokenToMerge = "";
                            }

                            /*if ( CAPITALIZE_WORDS == true ) {
                                sb.append(romaji.substring(0, 1).toUpperCase());
                                sb.append(romaji.substring(1));
                            } else {*/
                            if (token.getSurface().equals(token.getPronunciation()))
                                romaji = romaji.toUpperCase();
                            sb.append(romaji);
                            //}
                        }
                }
                sb.append(" ");
            }

            msg = sb.toString();

            if (!msg.isEmpty()) {
                message.getTextChannel().sendMessage(msg).queue();
            } else {
                printUsage(kyoko, l, message.getTextChannel());
            }
        }
    }
}
