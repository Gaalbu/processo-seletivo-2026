package br.com.lapes.commerce.auth;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklist {

  private static final String PREFIX = "blacklist:token:";

  private final StringRedisTemplate redisTemplate;

  public TokenBlacklist(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void invalidate(String token, Duration ttl) {
    redisTemplate.opsForValue().set(PREFIX + token, "1", ttl);
  }

  public boolean isInvalid(String token) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
  }
}
