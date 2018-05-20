package moe.kyokobot.music;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class MusicSettings {
    public AudioType type = AudioType.INTERNAL;
    public List<AudioNode> nodes = Arrays.asList(new AudioNode());

    public enum AudioType {
        INTERNAL, LAVALINK, NOZOMI;
    }

    public class AudioNode {
        public String host = "127.0.0.1";
        public String password = "";
        @SerializedName("ws-port")
        public int wsPort = 8080;
        @SerializedName("rest-port")
        public int restPort = 8081;
    }
}
