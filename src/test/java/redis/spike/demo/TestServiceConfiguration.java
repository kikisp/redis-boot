package redis.spike.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

@TestConfiguration
public class TestServiceConfiguration {

  @Bean
  @Primary
  public RedisConnectionFactory testRedisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
  }

  @Bean
  public Jedis jedis() {
    return new Jedis("localhost", 6379);
  }

  @Bean
  public RedisTemplate<String, String> authRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    final var authRedisTemplate = new RedisTemplate<String, String>();
    authRedisTemplate.setConnectionFactory(redisConnectionFactory);
    authRedisTemplate.afterPropertiesSet();

    return authRedisTemplate;
  }
}
