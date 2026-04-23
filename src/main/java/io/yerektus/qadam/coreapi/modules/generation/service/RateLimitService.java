package io.yerektus.qadam.coreapi.modules.generation.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.yerektus.qadam.coreapi.common.exception.RateLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<UUID, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int refillHours;

    public RateLimitService(
            @Value("${ratelimit.generation.capacity}") int capacity,
            @Value("${ratelimit.generation.refill-hours}") int refillHours
    ) {
        this.capacity = capacity;
        this.refillHours = refillHours;
    }

    /**
     * Checks if the user has available rate limit tokens.
     * Returns Mono.empty() if allowed, Mono.error(RateLimitException) if exhausted.
     */
    public Mono<Void> checkLimit(UUID userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, id -> buildBucket());
        if (bucket.tryConsume(1)) {
            return Mono.empty();
        }
        return Mono.error(new RateLimitException("Лимит " + capacity + " генераций в час исчерпан"));
    }

    private Bucket buildBucket() {
        Bandwidth limit = Bandwidth.classic(
                capacity,
                Refill.greedy(capacity, Duration.ofHours(refillHours))
        );
        return Bucket.builder().addLimit(limit).build();
    }
}
