package moe.kyokobot.bot.discordapi;

import moe.kyokobot.bot.discordapi.entity.Guild;
import moe.kyokobot.bot.discordapi.entity.TextChannel;
import moe.kyokobot.bot.discordapi.entity.User;

public interface DiscordAPI {
    void initialize() throws Exception;
    void shutdown();

    User getUser(long id);

    Guild getGuild(long id);
    TextChannel getChannel(Guild g, long id);

    void updateGuild(long id, Guild guild);
}
