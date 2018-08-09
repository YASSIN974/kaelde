package moe.kyokobot.commands.commands.fun;

import io.sentry.Sentry;
import moe.kyokobot.bot.command.Command;
import moe.kyokobot.bot.command.CommandCategory;
import moe.kyokobot.bot.command.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static moe.kyokobot.bot.util.RandomUtil.random;

public class WhatTheCommitCommand extends Command {
    private List<String> commitMessages;

    public WhatTheCommitCommand() {
        name = "whatthecommit";
        usage = "";
        aliases = new String[] {"wtc", "commit"};
        category = CommandCategory.FUN;
    }

    @Override
    public void onRegister() {
        commitMessages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/commit_messages.txt")))) {
            String line;
            while ((line = br.readLine()) != null)
                commitMessages.add(line);
        } catch (IOException e) {
            logger.error("Something went wrong while loading commit messages!", e);
            Sentry.capture(e);
        }
    }

    @Override
    public void execute(@NotNull CommandContext context) {
        context.send(commitMessages.get(random.nextInt(commitMessages.size())));
    }
}
