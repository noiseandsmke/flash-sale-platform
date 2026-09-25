package com.enterprise.flashsale.reservation.adapter.out.redis;

import com.enterprise.flashsale.reservation.application.port.out.TicketInventoryPort;
import com.enterprise.flashsale.reservation.domain.model.TicketId;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RedisTicketInventoryAdapter implements TicketInventoryPort {
    private static final String LOCK_KEY_PREFIX = "flashsale:ticket:";
    private static final String LOCK_KEY_SUFFIX = ":lock";

    private final StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;
    private final DefaultRedisScript<Long> reserveScript;
    private final DefaultRedisScript<Long> releaseScript;
    private final Timer reserveLuaTimer;
    private final Timer releaseLuaTimer;

    public RedisTicketInventoryAdapter(StringRedisTemplate redisTemplate, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.meterRegistry = meterRegistry;
        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setLocation(new ClassPathResource("scripts/reserve_stock.lua"));
        this.reserveScript.setResultType(Long.class);
        this.releaseScript = new DefaultRedisScript<>();
        this.releaseScript.setLocation(new ClassPathResource("scripts/release_stock.lua"));
        this.releaseScript.setResultType(Long.class);

        this.reserveLuaTimer = Timer.builder("flashsale.redis.lua.duration")
                .tag("script", "reserve_stock")
                .description("Execution duration of reserve_stock Redis Lua script")
                .publishPercentiles(0.99, 0.999)
                .register(meterRegistry);

        this.releaseLuaTimer = Timer.builder("flashsale.redis.lua.duration")
                .tag("script", "release_stock")
                .description("Execution duration of release_stock Redis Lua script")
                .publishPercentiles(0.99, 0.999)
                .register(meterRegistry);
    }

    @Override
    public boolean reserveStock(Long eventId, TicketId ticketId, String userId, long ttlSeconds) {
        String key = LOCK_KEY_PREFIX + ticketId.value() + LOCK_KEY_SUFFIX;
        List<String> keys = Collections.singletonList(key);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Long result = redisTemplate.execute(reserveScript, keys, userId, String.valueOf(ttlSeconds));
            return result != null && result == 1L;
        } finally {
            sample.stop(reserveLuaTimer);
        }
    }

    @Override
    public boolean releaseStock(TicketId ticketId, String userId) {
        String key = LOCK_KEY_PREFIX + ticketId.value() + LOCK_KEY_SUFFIX;
        List<String> keys = Collections.singletonList(key);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Long result = redisTemplate.execute(releaseScript, keys, userId);
            return result != null && result == 1L;
        } finally {
            sample.stop(releaseLuaTimer);
        }
    }
}
