package me.gabixdev.kyoko.bot.command.normal.basic;

import me.gabixdev.kyoko.bot.Constants;
import me.gabixdev.kyoko.bot.Kyoko;
import me.gabixdev.kyoko.bot.command.Command;
import me.gabixdev.kyoko.bot.command.CommandCategory;
import me.gabixdev.kyoko.bot.command.CommandContext;
import me.gabixdev.kyoko.bot.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class StatsCommand extends Command {
    private final RuntimeMXBean rb;
    private final Kyoko kyoko;

    public StatsCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.name = "stats";
        this.category = CommandCategory.BASIC;
        this.description = "stats.description";
        this.aliases = new String[]{"statistics", "info", "about"};
        this.rb = ManagementFactory.getRuntimeMXBean();
    }

    @Override
    public void execute(CommandContext context) {
        EmbedBuilder builder = context.getNormalEmbed()
                .setAuthor(context.getTranslated("stats.title"), Constants.SITE_URL, kyoko.getJda().getSelfUser().getAvatarUrl())
                .addField(context.getTranslated("stats.field.library"), "JDA " + JDAInfo.VERSION, true)
                .addField(context.getTranslated("stats.field.shard"), kyoko.getShardInfo(), true)
                .addField(context.getTranslated("stats.field.version"), Constants.VERSION, true)
                .addField(context.getTranslated("stats.field.uptime"), StringUtil.prettyPeriod(rb.getUptime()), true)
                .addField(context.getTranslated("stats.field.users"), Integer.toString(kyoko.getJda().getUsers().size()), true)
                .addField(context.getTranslated("stats.field.servers"), Integer.toString(kyoko.getJda().getGuilds().size()), true)
                .addField(context.getTranslated("stats.field.textchans"), Integer.toString(kyoko.getJda().getTextChannels().size()), true)
                .addField(context.getTranslated("stats.field.voicechans"), Integer.toString(kyoko.getJda().getVoiceChannels().size()), true)
                .addField(context.getTranslated("stats.field.os"), System.getProperty("os.name") + " " + System.getProperty("os.arch"), true)
                .addField(context.getTranslated("stats.field.github"), Constants.GITHUB_URL_MD, true)
                .addField(context.getTranslated("stats.field.website"), Constants.SITE_URL_MD, true)
                .addField(context.getTranslated("stats.field.donate"), "*soon*", true)
                .addField(context.getTranslated("stats.field.authors"), context.getTranslated("stats.authors"), true);
        context.send(builder.build());
    }
}
