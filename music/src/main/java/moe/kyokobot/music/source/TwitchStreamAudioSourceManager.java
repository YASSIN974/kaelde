package moe.kyokobot.music.source;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

/**
 * Audio source manager which detects Twitch tracks by URL.
 */
public class TwitchStreamAudioSourceManager extends com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager {
    /**
     * Create an instance.
     */
    public TwitchStreamAudioSourceManager() {
        super();
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        String streamName = getChannelIdentifierFromUrl(reference.identifier);
        if (streamName == null) {
            return null;
        }

        JsonBrowser channelInfo = fetchChannelInfo(streamName);

        if (channelInfo == null) {
            return AudioReference.NO_TRACK;
        } else {
            final String displayName = channelInfo.get("display_name").text();
            final String status = channelInfo.get("status").text();

            return new TwitchStreamAudioTrack(new AudioTrackInfo(
                    status,
                    displayName,
                    Long.MAX_VALUE,
                    reference.identifier,
                    true,
                    reference.identifier
            ), this);
        }
    }

    private JsonBrowser fetchChannelInfo(String name) {
        try (HttpInterface httpInterface = getHttpInterface()) {
            HttpUriRequest request = createGetRequest("https://api.twitch.tv/kraken/channels/" + name);

            return HttpClientTools.fetchResponseAsJson(httpInterface, request);
        } catch (IOException e) {
            throw new FriendlyException("Loading Twitch channel information failed.", SUSPICIOUS, e);
        }
    }
}

