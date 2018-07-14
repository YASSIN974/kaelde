package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.command.CommandIcons;
import net.dv8tion.jda.core.entities.GuildVoiceState;

public abstract class MusicCommand extends Command {
    protected boolean checkChannel = false;

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public String getUsage() {
        return usage == null ? "music." + name + ".usage" : usage;
    }

    @Override
    public String getDescription() {
        return description == null ? "music." + name + ".description" : description;
    }

    @Override
    public void preExecute(CommandContext context) {
        if (checkChannel) {
            GuildVoiceState botState = context.getGuild().getSelfMember().getVoiceState();
            GuildVoiceState userState = context.getMember().getVoiceState();
            if (botState.getChannel() != null && !userState.getChannel().equals(botState.getChannel())) {
                context.send(CommandIcons.ERROR + context.getTranslated("music.notinbotchannel"));
                return;
            }
        }

        super.preExecute(context);
    }
}
