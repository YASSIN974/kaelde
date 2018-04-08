package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.music.MusicUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;

public class NightcoreCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"nightcore"};

    public NightcoreCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
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
    public String getUsage() {
        return "music.nightcore.usage";
    }

    @Override
    public String getDescription() {
        return "music.nightcore.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        VoiceChannel vc = MusicUtil.getCurrentMemberChannel(message.getGuild(), message.getMember());
        if (vc == null) {
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            err.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.plsjoin"), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }

        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
        musicManager.outChannel = message.getTextChannel();

        EmbedBuilder eb = kyoko.getAbstractEmbedBuilder().getNormalBuilder();

        int mode = musicManager.sendHandler.getNightcore();
        if (args.length == 1) {
            if (mode == 6)
                mode = 0;
            else mode++;
        } else {
            mode = modeFromString(args[1]);
            if (mode == -1) {
                kyoko.getCommandManager().getCommand("help").handle(message, event, new String[] {"help", "nightcore"});
                return;
            }
        }
        eb.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.nightcore.mode." + mode), false);
        musicManager.sendHandler.setNightcore(mode);
        musicManager.sendHandler.updateFilters();
        message.getTextChannel().sendMessage(eb.build()).queue();
    }

    private int modeFromString(String in) {
        switch (in) {
            case "off":
            case "none":
            case "0":
                return 0;
            case "nightcore":
            case "1.5":
            case "1.5x":
            case "1":
                return 1;
            case "nightcore2":
            case "1.33":
            case "1.33x":
            case "2":
                return 2;
            case "nightcore3":
            case "1.25":
            case "1.25x":
            case "3":
                return 3;
            case "daycore":
            case "0.66":
            case "0.66x":
            case "4":
                return 4;
            case "daycore2":
            case "0.75":
            case "0.75x":
            case "5":
                return 5;
            case "daycore3":
            case "0.9":
            case "0.9x":
            case "6":
                return 6;
            default:
                return -1;
        }
    }
}
