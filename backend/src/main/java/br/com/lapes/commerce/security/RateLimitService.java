package br.com.lapes.commerce.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitService.class);
  private static final String PREFIX = "rate-limit:";

  private final ConcurrentMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();

  public RateLimitResult consume(String bucket, String key, int capacity, Duration window) {
    try {
      String cacheKey = PREFIX + bucket + ":" + key;
      Bucket bucket4j = bucketCache.computeIfAbsent(cacheKey, k -> newBucket(capacity, window));
      ConsumptionProbe probe = bucket4j.tryConsumeAndReturnRemaining(1);
      if (probe.isConsumed()) {
        return RateLimitResult.accepted();
      }
      return RateLimitResult.rejected(Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds() + 1);
    } catch (RedisConnectionFailureException exception) {
      LOGGER.warn("Redis unavailable for rate limiting; allowing request", exception);
      return RateLimitResult.accepted();
    }
  }

  private static Bucket newBucket(int capacity, Duration window) {
    return Bucket.builder()
        .addLimit(Bandwidth.classic(capacity, Refill.greedy(capacity, window)))
        .build();
  }
}
