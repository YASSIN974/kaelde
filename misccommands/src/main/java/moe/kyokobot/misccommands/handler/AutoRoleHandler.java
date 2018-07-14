package moe.kyokobot.misccommands.handler;

import com.google.common.eventbus.Subscribe;
import io.sentry.Sentry;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.manager.DatabaseManager;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoRoleHandler {

    private final Logger logger;
    private final DatabaseManager databaseManager;

    public AutoRoleHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Subscribe
    public void onJoin(GuildMemberJoinEvent event) {
        try {
            GuildConfig config = databaseManager.getGuild(event.getGuild());
            if (config.getModerationConfig() == null) return;

            if (!config.getModerationConfig().getAutoRole().isEmpty()) {
                Role role = event.getGuild().getRoleById(config.getModerationConfig().getAutoRole());
                if (role != null) {
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), role).queue();
                }
            }
        } catch (PermissionException e) {
            // ignore
        } catch (Exception e) {
            logger.error("Caught error in AutoRoleHandler!", e);
            Sentry.capture(e);
        }
    }
}
