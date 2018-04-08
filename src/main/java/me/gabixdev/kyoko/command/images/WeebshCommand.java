package me.gabixdev.kyoko.command.images;

import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WeebshCommand extends Command {
    private final Kyoko kyoko;
    private static final List<String> types;
    private static final List<String> titleTypes;
    private static final String cachedTypes;
    private HashMap<Guild, Long> cooldowns;

    static {
        types = Arrays.asList("awoo","bang","blush","clagwimoth","cry","cuddle","dance","hug","insult","jojo","kiss","lewd","lick","megumin","neko","nom","owo","pat","poke","pout","rem","shrug","slap","sleepy","smile","teehee","smug","stare","thumbsup","triggered","wag","waifu_insult","wasted","sumfuk","dab","tickle","highfive","banghead","bite","discord_memes","nani","initial_d","delet_this","poi","thinking","greet","punch","handholding","kemonomimi","trap","deredere","animal_cat","animal_dog");
        titleTypes = Arrays.asList("waaa", "discordmeme", "dance", "insult", "initiald", "trap", "kemonomimi", "triggered", "poi", "neko", "megumin");
        cachedTypes = "`" + String.join("`, `", types) + "`";
    }

    public WeebshCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.cooldowns = new HashMap<>();
        this.aliases = new String[]{"weebsh", "weeb"};
        this.label = aliases[0];
        this.description = "weebsh.description";
        this.usage = "weebsh.usage";
        this.category = CommandCategory.IMAGES;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (args.length == 1 || (args.length > 1 && !types.contains(args[1].toLowerCase()))) {
            printHelp(message.getTextChannel(), l);
        } else {
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

            EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

            Image image = kyoko.getWeeb4j().getRandomImage(getTitle(l, args[1]), HiddenMode.DEFAULT, message.getTextChannel().isNSFW() ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();
            eb.addField(args[1].toLowerCase(), Constants.POWERED_BY_WEEB, false);
            eb.setImage(image.getUrl());
            message.getTextChannel().sendMessage(eb.build()).queue();
        }
    }

    private void printHelp(TextChannel chan, Language l) {
        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append(kyoko.getI18n().get(l, "weebsh.types")).append("\n").append(cachedTypes);
        eb.addField(kyoko.getI18n().get(l, "weebsh.title"), sb.toString(), false);
        chan.sendMessage(eb.build()).queue();
    }

    private String getTitle(Language l, String type) {
        if (titleTypes.contains(type.toLowerCase())) {
            return kyoko.getI18n().get(l, "weebsh.description." + type.replace("_", ""));
        }
        return type;
    }
}
