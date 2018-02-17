package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;

public class NekosCommand extends Command
{
    private Kyoko kyoko;
    private final String[] types = new String[] {"neko", "kiss", "hug", "pat", "cuddle","lizard", "lewd"};
    private final String[] aliases = new String[] {"nekos", "neko"};
    private final String nekourl = "https://nekos.life/api/v2/img/";
    public NekosCommand(Kyoko kyoko)
    {
        this.kyoko = kyoko;
    }
    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return "nekos.description";
    }

    @Override
    public String getUsage() {
        return "nekos.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Settings settings = kyoko.getSettings();
        if(settings.isWipFeaturesEnabled())
        {
            Language l = kyoko.getI18n().getLanguage(message.getMember());
            if(args.length == 1) {
                String type = types[RandomUtils.nextInt(0, types.length-1)];
                String url = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(nekourl + type)).getAsJsonObject().get("url").getAsString();
                EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
                embedBuilder.addField(String.format(kyoko.getI18n().get(l, "nekos.title"), type), kyoko.getI18n().get(l, "nekos.subtitle"), true);
                embedBuilder.setImage(url);
                message.getTextChannel().sendMessage(embedBuilder.build()).queue();
                return;
            }
            if(!message.getTextChannel().isNSFW() && args[1].equalsIgnoreCase("lewd")) {
                printNSFW(kyoko, l, message.getTextChannel());
                return;
            }
            if(!Arrays.asList(types).contains(args[1].toLowerCase())) {
                printUsage(kyoko, l, message.getTextChannel());
                return;
            }
            String url = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(nekourl + args[1].toLowerCase())).getAsJsonObject().get("url").getAsString();
            EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            embedBuilder.addField(String.format(kyoko.getI18n().get(l, "nekos.title"), args[1].toLowerCase()), kyoko.getI18n().get(l, "nekos.subtitle"), true);
            embedBuilder.setImage(url);
            message.getTextChannel().sendMessage(embedBuilder.build()).queue();
        }


    }
}
