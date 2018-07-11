package moe.kyokobot.bot.util;

import moe.kyokobot.bot.Globals;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Paginator {
    public static final String LEFT_EMOJI = "\u25C0";
    public static final String RIGHT_EMOJI = "\u25B6";
    private static final String LEFT_CHEVRON = "445293361255940117";
    private static final String RIGHT_CHEVRON = "445293425601019926";

    protected int page = 0;
    protected String title;
    protected String footer;
    protected List<String> pageContents;
    protected EventWaiter eventWaiter;

    private Message message;
    private long messageId = 0;
    private long userId = 0;

    public Paginator(EventWaiter eventWaiter, List<String> pageContents, User user) {
        this.eventWaiter = eventWaiter;
        this.pageContents = pageContents;
        this.userId = user.getIdLong();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setPageContents(List<String> pageContents) {
        this.pageContents = pageContents;
    }

    public void setPage(int num) {
        page = num;
        if (page < 0) page = 0; else if (page > pageContents.size()) page = pageContents.size() - 1;
    }

    public void create(MessageChannel channel) {
        channel.sendMessage(render(page)).override(true).queue(message -> {
            this.message = message;
            messageId = message.getIdLong();
            if (pageContents.size() > 1)
                addReactions(message);
            waitForReaction();
        });
    }

    public void create(Message message) {
        message.editMessage(render(page)).override(true).queue();
        this.message = message;
        messageId = message.getIdLong();
        if (pageContents.size() > 1)
            addReactions(message);
        waitForReaction();
    }

    private void waitForReaction() {
        eventWaiter.waitForEvent(MessageReactionAddEvent.class,
                this::checkReaction,
                this::handleReaction, 15, TimeUnit.SECONDS, this::removeReactions);
    }

    protected Message render(int page) {
        MessageBuilder mb = new MessageBuilder();
        if (page < 0) page = 0; else if (page > pageContents.size()) page = pageContents.size() - 1;
        String content = pageContents.isEmpty() ? "" : pageContents.get(page);
        if (title != null) mb.append(title.replace("{page}", (page + 1) + "/" + pageContents.size())).append("\n");
        mb.append(content);
        if (footer != null) mb.append("\n").append(footer.replace("{page}", (page + 1) + "/" + pageContents.size()));
        return mb.build();
    }

    private void addReactions(Message message) {
        if (Globals.inKyokoServer) {
            message.addReaction("Previous-Page:" + LEFT_CHEVRON).queue(); // previous page
            message.addReaction("Next-Page:" + RIGHT_CHEVRON).queue(); // next page
        } else {
            message.addReaction(LEFT_EMOJI).queue();
            message.addReaction(RIGHT_EMOJI).queue();
        }
    }

    private void removeReactions() {
        message.getReactions().forEach(reaction -> {
            try {
                reaction.removeReaction().queue();
            } catch (PermissionException e) {
                // ignored
            }
        });
    }

    private boolean checkReaction(MessageReactionAddEvent event) {
        if (event.getMessageIdLong() == messageId) {
            if (event.getReactionEmote().isEmote()) {
                switch (event.getReactionEmote().getId()) {
                    case LEFT_CHEVRON:
                    case RIGHT_CHEVRON:
                        return event.getUser().getIdLong() == userId;
                    default:
                        return false;
                }
            } else {
                switch (event.getReactionEmote().getName()) {
                    case LEFT_EMOJI:
                    case RIGHT_EMOJI:
                        return event.getUser().getIdLong() == userId;
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    private void handleReaction(MessageReactionAddEvent event) {
        if (event.getReactionEmote().isEmote()) {
            switch (event.getReactionEmote().getId()) {
                case LEFT_CHEVRON:
                    if (page > 0) page--;
                    break;
                case RIGHT_CHEVRON:
                    if (page < (pageContents.size() - 1)) page++;
                    break;
            }
        } else {
            switch (event.getReactionEmote().getName()) {
                case LEFT_EMOJI:
                    if (page > 0) page--;
                    break;
                case RIGHT_EMOJI:
                    if (page < (pageContents.size() - 1)) page++;
                    break;
            }
        }

        try {
            event.getReaction().removeReaction(event.getUser()).queue();
        } catch (PermissionException e) {
            // ignored
        }

        message.editMessage(render(page)).override(true).queue(msg -> waitForReaction());
    }
}
