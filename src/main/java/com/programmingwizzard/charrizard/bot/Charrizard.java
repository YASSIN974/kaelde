package com.programmingwizzard.charrizard.bot;

import com.programmingwizzard.charrizard.bot.command.basic.DiscordCommands;
import pl.themolka.commons.command.Commands;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class Charrizard {

    private final Commands commands;

    public Charrizard() {
        this.commands = new DiscordCommands(this);
    }

    public void start() {

    }

    public void stop() {

    }

}
