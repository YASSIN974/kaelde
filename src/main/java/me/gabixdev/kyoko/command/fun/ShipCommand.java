package me.gabixdev.kyoko.command.fun;

import me.gabixdev.kyoko.Kyoko;
import me.gabixdev.kyoko.i18n.Language;
import me.gabixdev.kyoko.util.CommonErrorUtil;
import me.gabixdev.kyoko.util.UserUtil;
import me.gabixdev.kyoko.util.command.Command;
import me.gabixdev.kyoko.util.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ShipCommand extends Command {
    private Kyoko kyoko;
    private HashMap<Guild, Long> cooldowns;

    public ShipCommand(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.aliases = new String[] {"ship"};
        this.label = aliases[0];
        this.description = "ship.description";
        this.usage = "ship.usage";
        this.category = CommandCategory.FUN;
        this.cooldowns = new HashMap<>();
    }

    @Override
    public void handle(Message message, Event event, String[] args) throws Throwable {
        Language l = kyoko.getI18n().getLanguage(message.getMember());

        if (args.length == 1) {
            printUsage(kyoko, l, message.getTextChannel());
        } else {
            if (cooldowns.containsKey(message.getGuild())) {
                if (cooldowns.get(message.getGuild()) > System.currentTimeMillis()) {
                    CommonErrorUtil.cooldown(kyoko, l, message.getTextChannel());
                    return;
                } else {
                    cooldowns.remove(message.getGuild());
                    cooldowns.put(message.getGuild(), System.currentTimeMillis() + 5000);
                }
            } else {
                cooldowns.put(message.getGuild(), System.currentTimeMillis() + 5000);
            }

            String msg = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
            String[] users = msg.split(" x ");
            if (users.length != 2) {
                printUsage(kyoko, l, message.getTextChannel());
            } else {
                Member m1 = UserUtil.getMember(message.getGuild(), users[0]);
                if (m1 == null) {
                    CommonErrorUtil.noUserFound(kyoko, l, message.getTextChannel(), users[0]);
                    return;
                }

                Member m2 = UserUtil.getMember(message.getGuild(), users[1]);
                if (m2 == null) {
                    CommonErrorUtil.noUserFound(kyoko, l, message.getTextChannel(), users[1]);
                    return;
                }

                byte[] image = kyoko.getWeeb4j().generateLoveship(m1.getUser().getEffectiveAvatarUrl(), m2.getUser().getEffectiveAvatarUrl()).execute();

                MessageBuilder mb = new MessageBuilder();
                mb.append("**").append(m1.getEffectiveName()).append(" x ").append(m2.getEffectiveName()).append("**\n");
                int love = (m1.getUser().getId() + m2.getUser().getId()).hashCode() % 100;
                mb.append("love meter: ").append(Math.abs(love)).append("%");

                message.getTextChannel().sendFile(image, "ship.png", mb.build()).queue();
            }
        }
    }
}
