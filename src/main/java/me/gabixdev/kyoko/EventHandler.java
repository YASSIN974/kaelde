package me.gabixdev.kyoko;

import me.gabixdev.kyoko.util.command.Command;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/*
 * @author ProgrammingWizzard
 * @date 27.02.2017
 */
public class EventHandler implements EventListener {

    private final Kyoko kyoko;
    private final String pref;
    private int prefLen;

    private String mention;

    public EventHandler(Kyoko kyoko) {
        this.kyoko = kyoko;
        this.pref = kyoko.getSettings().getPrefix();
        this.prefLen = this.pref.length();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getAuthor().isBot()) return;

            if (mention == null) mention = kyoko.getJda().getSelfUser().getAsMention();

            String[] bits = e.getMessage().getRawContent().split(" ");

            if (bits.length == 0) return;

            // TODO: per-guild prefix
            if (bits[0].toLowerCase().startsWith(mention)) {
                if (bits.length == 1) return;

                String[] args = new String[bits.length - 1];
                System.arraycopy(bits, 1, args, 0, args.length);

                Command c = kyoko.getCommandManager().getHandler(args[0]);
                if (c != null) {
                    try {
                        kyoko.getCommandManager().incrementRunCount();
                        c.handle(e.getMessage(), e, args);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        // TODO: i18n
                        e.getMessage().getTextChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().setTitle("Kyoko").addField("Error", "Something bad happened, please report to bot authors.", false).build());
                    }
                }
            } else if (bits[0].toLowerCase().startsWith(pref)) {
                bits[0] = bits[0].substring(prefLen).trim();

                Command c = kyoko.getCommandManager().getHandler(bits[0]);
                if (c != null) {
                    try {
                        kyoko.getCommandManager().incrementRunCount();
                        c.handle(e.getMessage(), e, bits);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        // TODO: i18n
                        e.getMessage().getTextChannel().sendMessage(kyoko.getAbstractEmbedBuilder().getErrorBuilder().addField("Error", "Something bad happened, please report to bot authors.", true).build()).queue();
                    }
                }
            }
        }
    }
}
