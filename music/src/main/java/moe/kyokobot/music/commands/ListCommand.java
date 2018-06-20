package moe.kyokobot.music.commands;

import moe.kyokobot.bot.Globals;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.EmbedBuilder;
import moe.kyokobot.bot.util.EmbedPaginator;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicQueue;

import java.util.List;
import java.util.stream.Collectors;

import static moe.kyokobot.music.MusicIcons.MUSIC;

public class ListCommand extends MusicCommand {
    private final MusicManager manager;
    private final EventWaiter waiter;

    public ListCommand(MusicManager manager, EventWaiter waiter) {
        this.manager = manager;
        this.waiter = waiter;

        name = "list";
        description = "music.list.description";
    }

    @Override
    public void execute(CommandContext context) {
        MusicQueue queue = manager.getQueue(context.getGuild());
        if (queue.getTracks().size() == 0) {
            EmbedBuilder eb = context.getNormalEmbed();
            eb.setTitle((Globals.inKyokoServer ? "<:iclist:435576062894931968> " : "") + context.getTranslated("music.list.title"));
            eb.setDescription(context.getTranslated("music.queueempty").replace("{prefix}", context.getPrefix()));
            context.send(eb.build());
        } else {
            List<String> pages = StringUtil.createRawPages(queue.getTracks().stream().map(track -> track.getInfo().title.length() > 60 ? track.getInfo().title.substring(0, 60) + "..." : track.getInfo().title).collect(Collectors.toList()));
            EmbedPaginator paginator = new EmbedPaginator(waiter, pages, context.getSender(), context.getNormalEmbed());
            paginator.setTitle(MUSIC + context.getTranslated("music.list.title") + " ({page})");
            paginator.setTop("```markdown");
            paginator.setBottom("```");
            paginator.create(context.getChannel());
        }
    }
}
