package moe.kyokobot.bot.util.xmp;

import lombok.Getter;

@Getter
public class Module {
    private final long instance;	// KXMP instance

    private String name;			// Module title
    private String type;			// Module format
    private int numPatterns;		// Number of patterns
    private int numChannels;		// Number of channels
    private int numInstruments;		// Number of instruments
    private int numSamples;			// Number of samples
    private int initialSpeed;		// Initial speed
    private int initialBpm;			// Initial BPM
    private int length; // Module length in patterns

    public Module(Player player) {
        instance = player.getInstance();

        Xmp.getModData(instance, this);
    }
}
