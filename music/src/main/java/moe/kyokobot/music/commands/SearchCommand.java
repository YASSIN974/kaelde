package moe.kyokobot.music.commands;

import moe.kyokobot.bot.command.CommandContext;
import moe.kyokobot.music.MusicManager;
import moe.kyokobot.music.SearchManager;

public class SearchCommand extends MusicCommand {

    private final MusicManager musicManager;
    private final SearchManager searchManager;

    public SearchCommand(MusicManager musicManager, SearchManager searchManager) {
        this.musicManager = musicManager;
        this.searchManager = searchManager;

        name = "search";
    }

    @Override
    public void execute(CommandContext context) {

    }
}
