package samophis.lavalink.client.entities;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import moe.kyokobot.music.MusicPlayer;
import samophis.lavalink.client.entities.events.AudioEventListener;
import samophis.lavalink.client.entities.events.PlayerEvent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents a Guild-specific player instance similar to the default Lavalink Client's Link + IPlayer classes.
 * <br><p>This is the second most important class in LavaClient after, well, LavaClient, and controls all playback/update methods + more.</p>
 *
 * @author SamOphis
 * @since 0.1
 */

@SuppressWarnings("unused")
public interface LavaPlayer extends MusicPlayer {
    /**
     * Fetches the {@link AudioNode AudioNode} this player will attempt to send data to.
     * @return The {@link AudioNode AudioNode} this player will attempt to send all data to.
     */
    AudioNode getConnectedNode();

    /**
     * Fetches the Lavaplayer AudioTrack currently playing, as updated internally by LavaClient.
     * @return The Lavaplayer AudioTrack currently playing.
     */
    AudioTrack getPlayingTrack();

    /**
     * Fetches the {@link LavaClient LavaClient} instance this player is attached to.
     * @return The {@link LavaClient LavaClient} instance this player is associated with.
     */
    LavaClient getClient();

    /**
     * Retrieves an unmodifiable list of all the {@link AudioEventListener listeners} attached to this player.
     * @return An unmodifiable view of every {@link AudioEventListener listener} this player is aware of.
     */
    List<AudioEventListener> getListeners();

    /**
     * Fetches the ID of the Guild this player is associated with.
     * @return The ID of the Guild this player sends and receives events for.
     */
    long getGuildId();

    /**
     * Fetches the ID of the Voice Channel this player is associated with.
     * <br><p>Note: The Channel ID is never set internally by LavaClient as it relies on voice updates from the user. It's unreliable as it's easily forgotten to be set.</p>
     * @return The ID of the Voice Channel this player is connected to.
     */
    long getChannelId();

    /**
     * Fetches the UNIX Timestamp as provided by the player update events from the Lavalink-Server.
     * @return The UNIX Timestamp given to the client by the Lavalink-Server.
     */
    long getTimestamp();

    /**
     * Fetches The position of the player.
     * @throws IllegalStateException If the player isn't currently playing anything.
     * @return The position of the player.
     */
    long getPosition();

    /**
     * Fetches the current volume of the player.
     * @return The current volume of the player which songs are played at.
     */
    int getVolume();

    /**
     * Whether or not the player is currently paused.
     * @return Whether or not the player is currently paused.
     */
    boolean isPaused();

    /**
     * Adds a listener to this player.
     * @throws NullPointerException If the provided listener is null.
     * @param listener The <b>non-null</b> listener to add to the player.
     */
    void addListener(@Nonnull AudioEventListener listener);

    /**
     * Plays a track using the best, available {@link AudioNode AudioNode}.
     * <br><p>Note: Playing an AudioTrack directly saves a little bit of latency but also puts more load on the client and forces you to implement Lavaplayer on a wider scale.
     * <br>It's your choice whether or not you wish to load songs remotely via an identifier or via a track. Both work perfectly and all methods do cache, removing some latency/load.</p>
     * @throws NullPointerException If the provided AudioTrack is null.
     * @param track The <b>non-null</b> track to play.
     */
    void playTrack(@Nonnull AudioTrack track);

    /**
     * Plays a track using the best, available {@link AudioNode AudioNode}.
     * <br><p>Note: Playing an AudioTrack directly saves a little bit of latency but also puts more load on the client and forces you to implement Lavaplayer on a wider scale.
     * <br>It's your choice whether or not you wish to load songs remotely via an identifier or via track. Both work perfectly and all methods do cache, removing some latency/load.</p>
     * @throws NullPointerException If the provided AudioTrack is null.
     * @throws IllegalArgumentException If the ending time is smaller than the starting time.
     * @param track The <b>non-null</b> AudioTrack to play.
     * @param startTime The starting time of the track, useful for playing only sections of a track. Leave as 0 <b>(not negative one)</b> to start from the beginning of the track.
     * @param endTime The ending time of the track, useful for playing only sections of a track. Leave as -1 <b>(not zero)</b> to play what's left of the track from the start time.
     */
    void playTrack(@Nonnull AudioTrack track, long startTime, long endTime);

    /**
     * Plays a track using the best, available {@link AudioNode AudioNode}.
     * <br><p>Note: Playing a track via an identifier adds a little bit of latency but also puts less load on the client and removes the need to implement Lavaplayer more widely.
     * <br>It's your choice whether or not you wish to load songs remotely via an identifier or via a track. Both work perfectly and all methods do cache, removing some latency/load.</p>
     * @throws NullPointerException If the provided identifier is null.
     * @param identifier The <b>non-null</b> identifier from which to load an AudioTrack.
     */

    void playTrack(@Nonnull String identifier);
    /**
     * Plays a track using the best, available {@link AudioNode AudioNode}.
     * <br><p>Note: Playing a track via an identifier adds a little bit of latency but also puts less load on the client and removes the need to implement Lavaplayer more widely.
     * <br>It's your choice whether or not you wish to load songs remotely via an identifier or via a track. Both work perfectly and all methods do cache, removing some latency/load..</p>
     * @throws NullPointerException If the provided identifier is null.
     * @throws IllegalArgumentException If the ending time is smaller than the starting time.
     * @param identifier The <b>non-null</b> identifier from which to load an AudioTrack.
     * @param startTime The starting time of the track, useful for playing only sections of a track. Leave as 0 <b>(not negative one)</b> to start from the beginning of the track.
     * @param endTime The ending time of the track, useful for playing only sections of a track. Leave as -1 <b>(not zero)</b> to play what's left of the track from the start time.
     */
    void playTrack(@Nonnull String identifier, long startTime, long endTime);

    /**
     * Requests the Lavalink-Server to stop playback.
     */
    void stopTrack();

    /**
     * Sets whether or not to pause playback.
     * <br><p>Fires a PlayerPauseEvent or a PlayerResumeEvent.</p>
     * @param isPaused If the player should pause.
     */
    void setPaused(boolean isPaused);

    /**
     * Requests the Lavalink-Server to destroy the player.
     * <br><p><b>Note: It's VERY important to realize that the LavaPlayer object will maintain its state, allowing for future reconnects at the same position.</b></p>
     */
    void destroyPlayer();

    /**
     * Skips to a certain position in a track.
     * @param position The position, in milliseconds, to skip to.
     */
    void seek(long position);

    /**
     * Sets the volume of playback.
     * <br><p>Note: If the provided volume is negative or over 150, this method will return early instead of sending a volume update.</p>
     * @param volume The volume to set playback to, automatically bounded from 0 to 150 inclusive.
     */
    void setVolume(int volume);

    /**
     * Sets the node to connect and send data to.
     * @throws NullPointerException If the {@link AudioNode node} provided is null.
     * @param node the <b>non-null</b> {@link AudioNode AudioNode} to connect and send data to.
     */
    void setNode(@Nonnull AudioNode node);

    /**
     * Emits an {@link PlayerEvent event} to all {@link AudioEventListener listeners} attached to the player.
     * <br><p>Note: This uses reflection to determine the method to fire and thus shouldn't be used quickly/abusively.</p>
     * @throws NullPointerException If the event provided is null.
     * @param event The <b>non-null</b> event to fire.
     */
    void emitEvent(@Nonnull PlayerEvent event);
}