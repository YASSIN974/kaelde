package moe.kyokobot.weeb.commands;

import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import moe.kyokobot.bot.Constants;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.CommonErrors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WeebCommand extends Command {
    private static final List<String> types;
    private static final List<String> titleTypes;
    private static final String cachedTypes;
    private HashMap<Guild, Long> cooldowns;

    private Weeb4J weeb4J;

    static {
        types = Arrays.asList("awoo","bang","blush","clagwimoth","cry","cuddle","dance","hug","insult","jojo","kiss","lewd","lick","megumin","neko","nom","owo","pat","poke","pout","rem","shrug","slap","sleepy","smile","teehee","smug","stare","thumbsup","triggered","wag","waifu_insult","wasted","sumfuk","dab","tickle","highfive","banghead","bite","discord_memes","nani","initial_d","delet_this","poi","thinking","greet","punch","handholding","kemonomimi","trap","deredere","animal_cat","animal_dog");
        titleTypes = Arrays.asList("waaa", "discordmeme", "dance", "insult", "initiald", "trap", "kemonomimi", "triggered", "poi", "neko", "megumin");
        cachedTypes = "`" + String.join("`, `", types) + "`";
    }

    public WeebCommand(Weeb4J weeb4J) {
        this.weeb4J = weeb4J;
        this.cooldowns = new HashMap<>();
        this.aliases = new String[]{"weeb"};
        this.name = "weebsh";
        this.description = "weebsh.description";
        this.usage = "weebsh.usage";
        this.category = CommandCategory.IMAGES;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getConcatArgs().isEmpty() || !types.contains(context.getConcatArgs().toLowerCase())) {
            printHelp(context);
        } else {
            if (cooldowns.containsKey(context.getGuild())) {
                if (cooldowns.get(context.getGuild()) > System.currentTimeMillis()) {
                    CommonErrors.cooldown(context);
                    return;
                } else {
                    cooldowns.remove(context.getGuild());
                    cooldowns.put(context.getGuild(), System.currentTimeMillis() + 2000);
                }
            } else {
                cooldowns.put(context.getGuild(), System.currentTimeMillis() + 2000);
            }

            EmbedBuilder eb = context.getNormalEmbed();

            Image image = weeb4J.getRandomImage(context.getConcatArgs(), HiddenMode.DEFAULT, context.getChannel().isNSFW() ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();
            eb.addField(getTitle(context, context.getConcatArgs().toLowerCase()), Constants.POWERED_BY_WEEB, false);
            eb.setImage(image.getUrl());
            context.send(eb.build());
        }
    }

    private void printHelp(CommandContext context) {
        EmbedBuilder eb = context.getNormalEmbed();
        StringBuilder sb = new StringBuilder();
        sb.append(context.getTranslated("weebsh.types")).append("\n").append(cachedTypes);
        eb.addField(context.getTranslated("weebsh.title"), sb.toString(), false);
        context.send(eb.build());
    }

    private String getTitle(CommandContext context, String type) {
        if (titleTypes.contains(type.toLowerCase())) {
            return context.getTranslated("weebsh.description." + type.replace("_", "")) + (type.equalsIgnoreCase("trap") ? " <:lewd:404068578015576087>" : "");
        }
        return type;
    }
}
