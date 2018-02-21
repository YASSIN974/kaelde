package me.gabixdev.kyoko.util;

import me.gabixdev.kyoko.Kyoko;
import net.dv8tion.jda.core.entities.Icon;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class APICommands {
    public static void execCommand(Kyoko kyoko) {
        String command = System.getProperty("kyoko.apicommand", "");
        Logger log = kyoko.getLog();

        switch (command) {
            case "avatarUpdate":
                File f = new File("avatar.png");
                if (f.exists()) {
                    try {
                        kyoko.getJda().getSelfUser().getManager().setAvatar(Icon.from(f)).complete();
                        log.info("Avatar changed!");
                    } catch (IOException e) {
                        log.severe("Can't read avatar file!");
                        e.printStackTrace();
                    }
                } else {
                    log.warning("Requested avatar change, but file does not exists. Place it as \"avatar.png\"");
                }
                break;
            case "nameUpdate":
                kyoko.getJda().getSelfUser().getManager().setName(System.getProperty("kyoko.newname", "Kyoko")).queue();
                log.info("Name updated!");
                break;
            case "listGuilds":
                System.out.println("I am on " + kyoko.getJda().getGuilds().size() + " guilds:");
                kyoko.getJda().getGuilds().forEach(g -> System.out.println(g.getName() + " (" + g.getId() + ") " + g.getMembers().size() + " members"));
                break;
        }
    }
}
