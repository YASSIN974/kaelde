package moe.kyokobot.bot.util;

import lombok.Getter;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {
    Map<Long, RateLimit> rateLimits = new WeakHashMap<>();

    public RateLimiter() {

    }

    @Getter
    public class RateLimit {
        private AtomicInteger attempts;
        private long end;
    }
}
