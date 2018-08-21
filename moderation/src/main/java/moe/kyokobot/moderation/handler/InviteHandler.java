package moe.kyokobot.moderation.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.eventbus.Subscribe;
import moe.kyokobot.bot.entity.GuildConfig;
import moe.kyokobot.bot.event.DatabaseUpdateEvent;
import moe.kyokobot.bot.manager.DatabaseManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class InviteHandler {
    private static final Pattern inviteRegex = Pattern.compile("(https?://)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com/invite)/.+[0-9a-z]", CASE_INSENSITIVE);

    private final Cache<String, GuildConfig.ModerationConfig> modConfigCache = Caffeine.newBuilder().maximumSize(100).expireAfterAccess(5, TimeUnit.MINUTES).build();
    private final DatabaseManager databaseManager;

    public InviteHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Subscribe
    public void onMessage(MessageReceivedEvent event) {
        GuildConfig.ModerationConfig mcfg = modConfigCache.get(event.getMessage().getGuild().getId(), __ ->
                databaseManager.getGuild(event.getMessage().getGuild()).getModerationConfig());

        if (mcfg != null && mcfg.isAntiInvite()) {
            if (inviteRegex.matcher(event.getMessage().getContentRaw()).matches()) {
                event.getMessage().delete().queue();
                event.getMessage().getTextChannel().sendMessage("Do not send invites you skiddo.").queue();
            }
        }
    }

    @Subscribe
    public void onDatabaseUpdate(DatabaseUpdateEvent event) {
        if (event.getEntity() instanceof GuildConfig) {
            modConfigCache.invalidate(((GuildConfig) event.getEntity()).getGuildId());
        }
    }
}
