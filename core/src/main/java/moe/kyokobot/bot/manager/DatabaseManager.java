package moe.kyokobot.bot.manager;

import moe.kyokobot.bot.entity.DatabaseEntity;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.entity.UserConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface DatabaseManager {
    void load();

    UserConfig getUser(User user) throws Exception;

    GuildConfig getGuild(Guild guild) throws Exception;

    Map<String, Integer> getTopBalances();

    String getValue(User user, String key, String def);

    String getValue(User user, String key);

    List<String> getList(User user, String key, List<String> def);

    List<String> getList(User user, String key);

    void save(@NotNull DatabaseEntity entity);
}
