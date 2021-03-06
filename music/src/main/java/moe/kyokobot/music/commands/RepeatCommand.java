package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.bot.util.StringUtil;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.MusicQueue;
import org.jetbrains.annotations.NotNull;

import static moe.kyokobot.music.MusicIcons.REPEAT;

public class RepeatCommand extends MusicCommand {
    private final MusicManager musicManager;

    public RepeatCommand(MusicManager musicManager) {
        this.musicManager = musicManager;
        name = "repeat";
        usage = "";
        description = "music.repeat.description";
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        MusicQueue queue = musicManager.getQueue(context.getGuild());
        queue.setRepeating(!queue.isRepeating());
        context.send(REPEAT + String.format(context.getTranslated("music.repeattoggle"), StringUtil.toggleFormat(context, queue.isRepeating())));
    }
}
