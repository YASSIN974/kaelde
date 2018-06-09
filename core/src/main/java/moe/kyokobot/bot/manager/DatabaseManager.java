package moe.kyokobot.bot.manager;

import moe.kyokobot.bot.entity.DatabaseEntity;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

public interface DatabaseManager {
    void load();

    UserConfig getUser(User user) throws Exception;

    GuildConfig getGuild(Guild guild) throws Exception;

    void save(@NotNull DatabaseEntity entity);
}
