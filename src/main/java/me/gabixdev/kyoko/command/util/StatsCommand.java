package me.gabixdev.kyoko.command.util;

import me.gabixdev.kyoko.Constants;
import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.StringUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class StatsCommand extends Command {

    private final RuntimeMXBean rb;


    private final String[] aliases = new String[]{"stats", "statistics"};
    private Kyoko kyoko;

    public StatsCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.rb = ManagementFactory.getRuntimeMXBean();
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
        return "stats.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.UTILITY;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        EmbedBuilder builder = kyoko.getAbstractEmbedBuilder().getNormalBuilder()
                .setAuthor(kyoko.getI18n().get(l, "stats.title"), Constants.SITE_URL, kyoko.getJda().getSelfUser().getAvatarUrl())
                .addField(kyoko.getI18n().get(l, "stats.field.library"), "JDA " + JDAInfo.VERSION, true)
                .addField(kyoko.getI18n().get(l, "stats.field.shard"), kyoko.getShardInfo(), true)
                .addField(kyoko.getI18n().get(l, "stats.field.version"), Constants.VERSION, true)
                .addField(kyoko.getI18n().get(l, "stats.field.uptime"), StringUtil.prettyPeriod(rb.getUptime()), true)
                .addField(kyoko.getI18n().get(l, "stats.field.users"), Integer.toString(kyoko.getJda().getUsers().size()), true)
                .addField(kyoko.getI18n().get(l, "stats.field.servers"), Integer.toString(kyoko.getJda().getGuilds().size()), true)
                .addField(kyoko.getI18n().get(l, "stats.field.textchans"), Integer.toString(kyoko.getJda().getTextChannels().size()), true)
                .addField(kyoko.getI18n().get(l, "stats.field.voicechans"), Integer.toString(kyoko.getJda().getVoiceChannels().size()), true)
                .addField(kyoko.getI18n().get(l, "stats.field.cmdsran"), Long.toString(kyoko.getCommandManager().getCommandRunCount()), true)
                .addField(kyoko.getI18n().get(l, "stats.field.github"), Constants.GITHUB_URL_MD, true)
                .addField(kyoko.getI18n().get(l, "stats.field.website"), Constants.SITE_URL_MD, true)
                .addField(kyoko.getI18n().get(l, "stats.field.donate"), "*soon*", true)
                .addField(kyoko.getI18n().get(l, "stats.field.authors"), kyoko.getI18n().get(l, "stats.authors"), true);
        message.getChannel().sendMessage(builder.build()).queue();
    }
}
