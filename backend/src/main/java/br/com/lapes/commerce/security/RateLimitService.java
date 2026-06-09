package br.com.lapes.commerce.security;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitService.class);
  private static final String PREFIX = "rate-limit:";

  private final StringRedisTemplate redisTemplate;

  public RateLimitService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public RateLimitResult consume(String bucket, String key, int capacity, Duration window) {
    try {
      String redisKey = PREFIX + bucket + ":" + key;
      Long current = redisTemplate.opsForValue().increment(redisKey);
      if (current != null && current == 1L) {
        redisTemplate.expire(redisKey, window);
      }

      if (current == null || current <= capacity) {
        return RateLimitResult.accepted();
      }

      Long ttl = redisTemplate.getExpire(redisKey);
      return RateLimitResult.rejected(ttl == null || ttl < 0 ? window.toSeconds() : ttl);
    } catch (RedisConnectionFailureException exception) {
      LOGGER.warn("Redis unavailable for rate limiting; allowing request", exception);
      return RateLimitResult.accepted();
    }
  }
}
