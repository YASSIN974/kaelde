package moe.kyokobot.bot.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class EmbedPaginator extends Paginator {
    private EmbedBuilder builder;
    private String top;

    public EmbedPaginator(EventWaiter eventWaiter, List<String> pageContents, User user, EmbedBuilder builder) {
        super(eventWaiter, pageContents, user);
        this.builder = builder;
    }

    public void setTop(String top) {
        this.top = top;
    }

    @Override
    protected Message render(int page) {
        MessageBuilder mb = new MessageBuilder();
        if (page < 0) page = 0; else if (page > pageContents.size()) page = pageContents.size() - 1;
        String content = (top == null ? "" :  top + "\n") + (pageContents.size() == 0 ? "" : pageContents.get(page)) + (footer == null ? "" : "\n" + footer);
        builder.setTitle(title.replace("{page}", (page + 1) + "/" + pageContents.size()));
        builder.setDescription(content);
        mb.setEmbed(builder.build());
        return mb.build();
    }
}
