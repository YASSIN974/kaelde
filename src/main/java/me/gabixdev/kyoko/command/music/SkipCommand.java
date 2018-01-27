package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

public class SkipCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"skip"};

    public SkipCommand(Kyoko kyoko) {
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
    public String getDescription() {
        return "music.skip.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());
        musicManager.outChannel = message.getTextChannel();

        if (musicManager.scheduler.getQueue().isEmpty()) {
            musicManager.player.stopTrack();
            EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
            err.addField(kyoko.getI18n().get(l, "music.title"), String.format(kyoko.getI18n().get(l, "music.msg.empty"), kyoko.getSettings().getPrefix()), false);
            message.getChannel().sendMessage(err.build()).queue();
            return;
        }

        if (args.length == 2) {
            try {
                int i = Integer.parseUnsignedInt(args[1]);

                if (i > musicManager.scheduler.getQueue().size()) i = musicManager.scheduler.getQueue().size() - 1;

                for (int a = 0; a < (i - 1); a++)
                    musicManager.scheduler.getQueue().poll();
            } catch (NumberFormatException e) {

            }
        }

        musicManager.scheduler.nextTrack();
        /*EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        err.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.skipped"), false);
        message.getChannel().sendMessage(err.build()).queue();*/
    }
}
