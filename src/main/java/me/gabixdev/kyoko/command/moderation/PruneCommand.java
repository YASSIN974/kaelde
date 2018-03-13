package me.gabixdev.kyoko.command.moderation;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PruneCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"prune", "delete"};
    private HashMap<Guild, Long> cooldowns;

    public PruneCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.cooldowns = new HashMap<>();
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
        return "mod.prune.description";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String getUsage() {
        return "mod.prune.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (args.length < 2) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        if (cooldowns.containsKey(message.getGuild())) {
            if (cooldowns.get(message.getGuild()) > System.currentTimeMillis()) {
                CommonErrorUtil.cooldown(kyoko, l, message.getTextChannel());
                return;
            } else {
                cooldowns.remove(message.getGuild());
                cooldowns.put(message.getGuild(), System.currentTimeMillis() + 5000);
            }
        } else {
            cooldowns.put(message.getGuild(), System.currentTimeMillis() + 5000);
        }

        if (message.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            try {
                try {
                    int messageAmount = Integer.parseInt(args[1]);
                    if (messageAmount < kyoko.getSettings().getMinRemove()) {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "mod.prune.minremove"), kyoko.getSettings().getMinRemove()), false);
                        message.getTextChannel().sendMessage(err.build()).queue();
                        return;
                    }

                    if (messageAmount > kyoko.getSettings().getMaxRemove()) {
                        EmbedBuilder err = kyoko.getAbstractEmbedBuilder().getErrorBuilder();
                        err.addField(kyoko.getI18n().get(l, "generic.error"), String.format(kyoko.getI18n().get(l, "mod.prune.maxremove"), kyoko.getSettings().getMaxRemove()), false);
                        message.getTextChannel().sendMessage(err.build()).queue();
                        return;
                    }

                    message.getTextChannel().getHistory().retrievePast(messageAmount).queue(list -> {
                        message.getTextChannel().deleteMessages(list).queue(success -> {
                            message.getTextChannel().sendMessage(String.format(kyoko.getI18n().get(l, "mod.prune.cleared"), list.size())).queue(completeMsg -> {
                                completeMsg.delete().completeAfter(5, TimeUnit.SECONDS);
                            }, failure -> {
                                failure.printStackTrace();
                                CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
                            });
                        }, failure -> {
                            failure.printStackTrace();
                            CommonErrorUtil.exception(kyoko, l, message.getTextChannel());
                        });
                    });

                } catch (NumberFormatException exception) {
                    CommonErrorUtil.notANumber(kyoko, l, message.getTextChannel(), args[1]);
                }
            } catch (PermissionException e) {
                CommonErrorUtil.noPermissionBot(kyoko, l, message.getTextChannel());
            }
        } else {
            CommonErrorUtil.noPermissionUser(kyoko, l, message.getTextChannel());
        }
    }
}
