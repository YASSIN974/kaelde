package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.EventWaiter;
import moe.kyokobot.bot.util.Paginator;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicQueue;

import java.util.List;
import java.util.stream.Collectors;

import static moe.kyokobot.music.MusicIcons.PLAY;

public class ListCommand extends Command {
    private final MusicManager manager;
    private final EventWaiter waiter;

    public ListCommand(MusicManager manager, EventWaiter waiter) {
        this.manager = manager;
        this.waiter = waiter;

        name = "list";
        category = CommandCategory.MUSIC;
        description = "music.list.description";
    }

    @Override
    public void execute(CommandContext context) {
        MusicQueue queue = manager.getQueue(context.getGuild());
        if (queue.getTracks().size() == 0) {
            context.send(context.error() + "Queue is empty!");
        } else {
            List<String> pages = StringUtil.createPages(queue.getTracks().stream().map(track -> track.getInfo().title.length() > 60 ? track.getInfo().title.substring(0, 60) + "..." : track.getInfo().title).collect(Collectors.toList()));
            Paginator paginator = new Paginator(waiter, pages, PLAY + "Track listing `[{page}]`", context.getSender());
            paginator.create(context.getChannel());
        }
    }
}
