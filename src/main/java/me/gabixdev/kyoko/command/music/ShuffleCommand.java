package me.gabixdev.kyoko.command.music;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.music.MusicManager;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.Collections;

public class ShuffleCommand extends Command
{
    private Kyoko kyoko;
    private final String[] aliases = new String[] {"shuffle"};
    public ShuffleCommand(Kyoko kyoko)
    {
        this.kyoko = kyoko;
    }

    @Override
    public String getDescription() {
        return "music.shuffle.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
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
    public void handle(Message message, Event event, String[] args) throws Throwable
    {
        Language l = kyoko.getI18n().getLanguage(message.getMember());
        MusicManager musicManager = kyoko.getMusicManager(message.getGuild());

        if(musicManager.scheduler.getQueue().size() <=1)
        {
            EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
            embedBuilder.addField(kyoko.getI18n().get(l, "generic.error"), kyoko.getI18n().get(l, "music.msg.cantbeshuffled"), false);
            message.getTextChannel().sendMessage(embedBuilder.build()).queue();
            return;
        }
        Collections.shuffle(musicManager.scheduler.getQueue());
        EmbedBuilder embedBuilder = kyoko.getAbstractEmbedBuilder().getNormalBuilder();
        embedBuilder.addField(kyoko.getI18n().get(l, "music.title"), kyoko.getI18n().get(l, "music.msg.shuffled"), false);
        message.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }
}
