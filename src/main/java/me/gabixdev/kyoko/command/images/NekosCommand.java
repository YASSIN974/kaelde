package me.gabixdev.kyoko.command.images;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.Settings;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.GsonUtil;
import me.gabixdev.kyoko.util.URLUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;
import java.util.HashMap;

public class NekosCommand extends Command {
    private Kyoko kyoko;
    private static final String[] types = new String[]{"neko", "kiss", "hug", "pat", "cuddle", "lizard", "lewd"};
    private static final String[] aliases = new String[]{"nekos"};
    public static final String NEKOS_URL = "https://nekos.life/api/v2/img/";
    private HashMap<Guild, Long> cooldowns;

    public NekosCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.cooldowns = new HashMap<>();
    }

    @Override
    public String getLabel() {
        return aliases[0];
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGES;
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
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (cooldowns.containsKey(message.getGuild())) {
            if (cooldowns.get(message.getGuild()) > System.currentTimeMillis()) {
                CommonErrorUtil.cooldown(kyoko, l, message.getTextChannel());
                return;
            } else {
                cooldowns.remove(message.getGuild());
                cooldowns.put(message.getGuild(), System.currentTimeMillis() + 2000);
            }
        } else {
            cooldowns.put(message.getGuild(), System.currentTimeMillis() + 2000);
        }

        if (args.length == 1) {
            String type = "neko";//types[RandomUtils.nextInt(0, types.length - 1)];
            String url = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(NEKOS_URL + type)).getAsJsonObject().get("url").getAsString();
            EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            embedBuilder.addField(String.format(kyoko.getI18n().get(l, "nekos.title"), type), Constants.POWERED_BY_NEKOSLIFE, true);
            embedBuilder.setImage(url);
            message.getTextChannel().sendMessage(embedBuilder.build()).queue();
            return;
        }

        if (!message.getTextChannel().isNSFW() && args[1].equalsIgnoreCase("lewd")) {
            printNSFW(kyoko, l, message.getTextChannel());
            return;
        }

        if (!Arrays.asList(types).contains(args[1].toLowerCase())) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        String url = GsonUtil.fromStringToJsonElement(URLUtil.readUrl(NEKOS_URL + args[1].toLowerCase())).getAsJsonObject().get("url").getAsString();
        EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        embedBuilder.addField(String.format(kyoko.getI18n().get(l, "nekos.title"), args[1].toLowerCase()), Constants.POWERED_BY_NEKOSLIFE, true);
        embedBuilder.setImage(url);
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }
}
