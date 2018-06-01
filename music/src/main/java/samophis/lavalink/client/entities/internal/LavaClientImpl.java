package samophis.lavalink.client.entities.internal;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import samophis.lavalink.client.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LavaClientImpl implements LavaClient {
    public static final Map<String, AudioNode> NODES = new Object2ObjectOpenHashMap<>();
    @SuppressWarnings("WeakerAccess")
    public static final Long2ObjectMap<LavaPlayer> PLAYERS = new Long2ObjectOpenHashMap<>();
    private final LavaHttpManager manager;
    private final String password;
    private final int restPort, wsPort, shards;
    private final long expireWriteMs, expireAccessMs, userId;
    private final Cache<String, TrackPair> identifierCache;
    public LavaClientImpl(String password, int restPort, int wsPort, int shards, long expireWriteMs, long expireAccessMs, long userId, List<AudioNodeEntry> entries) {
        this.manager = new LavaHttpManagerImpl(this);
        this.password = password;
        this.restPort = restPort;
        this.wsPort = wsPort;
        this.shards = shards;
        this.expireWriteMs = expireWriteMs;
        this.expireAccessMs = expireAccessMs;
        this.userId = userId;
        this.identifierCache = Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(5000)
                .expireAfterWrite(expireWriteMs, TimeUnit.MILLISECONDS)
                .expireAfterAccess(expireAccessMs, TimeUnit.MILLISECONDS)
                .build();
        entries.forEach(entry -> NODES.put(entry.getRawAddress() + entry.getWebSocketPort(), new AudioNodeImpl(this, entry)));
    }
    @Override
    public LavaHttpManager getHttpManager() {
        return manager;
    }
    @Override
    public String getGlobalServerPassword() {
        return password;
    }
    @Override
    public long getCacheExpireAfterWriteMs() {
        return expireWriteMs;
    }
    @Override
    public long getCacheExpireAfterAccessMs() {
        return expireAccessMs;
    }
    @Override
    public int getGlobalRestPort() {
        return restPort;
    }
    @Override
    public int getGlobalWebSocketPort() {
        return wsPort;
    }
    @Override
    public int getShardCount() {
        return shards;
    }
    @Override
    public long getUserId() {
        return userId;
    }
    @Override
    public void addEntry(AudioNodeEntry entry) {
        NODES.put(entry.getRawAddress() + entry.getWebSocketPort(), new AudioNodeImpl(this, entry));
    }
    @Override
    public List<AudioNode> getAudioNodes() {
        return Collections.unmodifiableList(new ArrayList<AudioNode>(NODES.values()));
    }
    @Override
    public List<LavaPlayer> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<LavaPlayer>(PLAYERS.values()));
    }
    @Override
    public LavaPlayer getPlayerByGuildId(long guild_id) {
        return PLAYERS.computeIfAbsent(guild_id, ignored -> new LavaPlayerImpl(this, guild_id));
    }
    @Override
    public AudioNode getNodeByIdentifier(String address, int websocketPort) {
        return NODES.get(address + websocketPort);
    }
    @Override
    public Cache<String, TrackPair> getIdentifierCache() {
        return identifierCache;
    }

    @Override
    public Long2ObjectMap<LavaPlayer> getRawPlayers() {
        return PLAYERS;
    }
}
