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
import moe.kyokobot.bot.util.CommonUtil;
import moe.kyokobot.bot.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.stream.Collectors;

import static moe.kyokobot.weeb.WeebConstants.COMMAND_RATELIMIT;
import static moe.kyokobot.weeb.WeebConstants.ICON_URL;

public class ActionCommand extends Command {
    private final Weeb4J weeb4J;
    private final HashMap<Guild, Long> cooldowns;

    public ActionCommand(Weeb4J weeb4J, HashMap<Guild, Long> cooldowns, String name) {
        this.weeb4J = weeb4J;
        this.cooldowns = cooldowns;
        this.name = name;
        category = CommandCategory.IMAGES;
        description = "weebsh.action." + name + ".description";
        usage = "generic.multipleusersusage";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.hasArgs()) {
            if (CommonUtil.checkCooldown(cooldowns, context, COMMAND_RATELIMIT)) return;

            try {
                Image image = weeb4J.getRandomImage(this.name, HiddenMode.DEFAULT, context.getChannel().isNSFW() ? NsfwFilter.ALLOW_NSFW : NsfwFilter.NO_NSFW).execute();

                String rawMessage = context.getEvent().getMessage().getContentRaw();
                String kyokoMention = context.getEvent().getJDA().getSelfUser().getAsMention();
                EmbedBuilder eb = context.getNormalEmbed();
                if (context.getEvent().getMessage().getMentionedUsers().stream().anyMatch(user -> user.getId().equals(context.getSender().getId()))) {
                    eb.setTitle(context.getTranslated("weebsh.action." + this.name + ".alone"));
                } else {
                    String users = context.getEvent().getMessage().getMentionedUsers().stream()
                            .filter(user -> !((rawMessage.startsWith(kyokoMention) && (StringUtil.getOccurencies(rawMessage, kyokoMention) < 2) && (user.getIdLong() == context.getEvent().getJDA().getSelfUser().getIdLong()))))
                            .map(user -> context.getGuild().getMember(user).getEffectiveName()).collect(Collectors.joining(", ")).trim();
                    eb.setTitle(String.format(context.getTranslated("weebsh.action." + this.name + ".someone"), context.getMember().getEffectiveName(), users));
                }

                eb.setImage(image.getUrl());
                eb.setFooter(Constants.POWERED_BY_WEEB, ICON_URL);
                context.send(eb.build());
            } catch (Exception e) {
                e.printStackTrace();
                CommonErrors.exception(context, e);
            }
        } else {
            context.send(context.error() + context.getTranslated("action.needtomention"));
        }
    }
}
