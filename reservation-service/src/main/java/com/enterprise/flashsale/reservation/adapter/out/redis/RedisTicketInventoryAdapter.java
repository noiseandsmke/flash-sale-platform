package com.enterprise.flashsale.reservation.adapter.out.redis;

import com.enterprise.flashsale.reservation.application.port.out.TicketInventoryPort;
import com.enterprise.flashsale.reservation.domain.model.TicketId;
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
    private final DefaultRedisScript<Long> reserveScript;
    private final DefaultRedisScript<Long> releaseScript;

    public RedisTicketInventoryAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setLocation(new ClassPathResource("scripts/reserve_stock.lua"));
        this.reserveScript.setResultType(Long.class);
        this.releaseScript = new DefaultRedisScript<>();
        this.releaseScript.setLocation(new ClassPathResource("scripts/release_stock.lua"));
        this.releaseScript.setResultType(Long.class);
    }

    @Override
    public boolean reserveStock(Long eventId, TicketId ticketId, String userId, long ttlSeconds) {
        String key = LOCK_KEY_PREFIX + ticketId.value() + LOCK_KEY_SUFFIX;
        List<String> keys = Collections.singletonList(key);

        Long result = redisTemplate.execute(reserveScript, keys, userId, String.valueOf(ttlSeconds));

        return result != null && result == 1L;
    }

    @Override
    public boolean releaseStock(TicketId ticketId, String userId) {
        String key = LOCK_KEY_PREFIX + ticketId.value() + LOCK_KEY_SUFFIX;
        List<String> keys = Collections.singletonList(key);

        Long result = redisTemplate.execute(releaseScript, keys, userId);

        return result != null && result == 1L;
    }
}
