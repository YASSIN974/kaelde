package moe.kyokobot.bot.event;

import lombok.Getter;
import lombok.Setter;
import moe.kyokobot.bot.command.CommandContext;

@Getter
public class CommandDispatchEvent {
    private final CommandContext context;
    @Setter
    private boolean cancelled;

    public CommandDispatchEvent(CommandContext context) {
        this.context = context;
    }
}
