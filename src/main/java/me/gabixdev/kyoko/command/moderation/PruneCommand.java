package me.gabixdev.kyoko.command.moderation;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PruneCommand extends Command {
    private Kyoko kyoko;
    private final String[] aliases = new String[]{"prune", "delete"};

    public PruneCommand(Kyoko kyoko) {
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
        return "mod.prune.description";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public String getUsage() {
        return "mod.prune.usage";
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getGuild());

        if (args.length < 2) {
            printUsage(kyoko, l, message.getTextChannel());
            return;
        }

        if (message.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
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

                int deleted = 0;
                List<Message> msgs;
                while (messageAmount != 0) {
                    if (messageAmount > 100) {
                        msgs = message.getTextChannel().getHistory().retrievePast(100).complete();
                        messageAmount -= 100;
                    } else {
                        msgs = message.getTextChannel().getHistory().retrievePast(messageAmount).complete();
                        messageAmount = 0;
                    }
                    msgs = msgs.stream().filter(msg -> !msg.getCreationTime().isBefore(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());

                    deleted += msgs.size();
                    if (msgs.size() > 1) {
                        final int d = deleted;
                        message.getTextChannel().deleteMessages(msgs).queue(success -> {
                            message.getTextChannel().sendMessage(String.format(kyoko.getI18n().get(l, "mod.prune.cleared"), d)).queue(completeMsg -> {
                                completeMsg.delete().completeAfter(5, TimeUnit.SECONDS);
                            });

                        });
                    } else return;
                }
            } catch (PermissionException e) {
                CommonErrorUtil.noPermissionBot(kyoko, l, message.getTextChannel());
            }
        } else {
            CommonErrorUtil.noPermissionUser(kyoko, l, message.getTextChannel());
        }
    }
}
